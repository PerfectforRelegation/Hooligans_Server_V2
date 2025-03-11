package com.joh.coin.service;

import com.joh.coin.dto.request.TradeOrder;
import com.joh.coin.dto.response.Message;
import com.joh.coin.entity.OrderBook;
import com.joh.coin.entity.utill.OrderStatus;
import com.joh.coin.entity.utill.OrderType;
import com.joh.coin.exception.FindOrderBookDataException;
import com.joh.coin.exception.OrderBookSaveException;
import com.joh.coin.handler.CoinWebSocketHandler;
import com.joh.coin.repository.OrderBookRepository;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.transaction.reactive.TransactionalOperator;

@Service
@RequiredArgsConstructor
public class OrderBookService {

  private final OrderBookRepository orderBookRepository;
  private final CoinWebSocketHandler coinWebSocketHandler;
  // 트랜잭션
  private final TransactionalOperator transactionalOperator;

  // 매수 메서드
  public Mono<Message> buyCoin(String userId, TradeOrder tradeOrder) {

    // 코인 아이디, 가격, 수량
    Long coinId = tradeOrder.getCoinId();
    Long price = tradeOrder.getPrice();
    Long amount = tradeOrder.getAmount();

    AtomicReference<Long> amountToBuy = new AtomicReference<>(amount);

    return findPendingOrders(coinId, price, OrderType.SELL.name())
        .concatMap(sellOrderBook -> { // 순차적 처리
          if (amountToBuy.get() == 0) {
            return Mono.empty(); // 매수량이 0이면 더 이상 진행하지 않음
          }

          return findCompletedOrdersFromSameRequestByStatus(sellOrderBook,
              OrderStatus.COMPLETED.name())
              .flatMap(completedOrderBook -> { // 체결된 주문이 존재하는 경우
                long sellAmount = sellOrderBook.getAmount();

                if (amountToBuy.get() <= sellAmount) {
                  long restAmount = sellAmount - amountToBuy.get();

                  if (restAmount == 0) { // 딱 떨어지는 경우
                    completedOrderBook.updateAmount(completedOrderBook.getAmount() + sellAmount);
                    amountToBuy.set(0L);

                    return orderBookRepository.save(completedOrderBook)
                        .then(orderBookRepository.delete(sellOrderBook)
                        );
                  } else {
                    completedOrderBook.updateAmount(
                        completedOrderBook.getAmount() + amountToBuy.get());
                    sellOrderBook.updateAmount(restAmount);
                    amountToBuy.set(0L);

                    return orderBookRepository.save(completedOrderBook)
                        .then(orderBookRepository.save(sellOrderBook));
                  }
                } else {
                  // sellAmount 보다 매수량이 많은 부분, 이전의 체결 데이터 있는 부분"
                  amountToBuy.getAndUpdate(current -> current - sellAmount); // 업데이트
                  completedOrderBook.updateAmount(
                      completedOrderBook.getAmount() + sellOrderBook.getAmount());

                  return Mono.when(
                      orderBookRepository.save(completedOrderBook),
                      orderBookRepository.delete(sellOrderBook)
                  ).then(Mono.just(true)); // 웬 true 반환? 이거 없으면 switchIfEmpty 메서드가 실행됨;;
                }
              })
              .switchIfEmpty(Mono.defer(() -> { // 체결된 주문이 없을 때 처리
                long sellAmount = sellOrderBook.getAmount();

                if (amountToBuy.get() <= sellAmount) {

                  long restAmount = sellAmount - amountToBuy.get();

                  if (restAmount == 0) { // 딱 떨어질 때
                    sellOrderBook.updateStatusToCompleted();
                    amountToBuy.set(0L);

                    return orderBookRepository.save(sellOrderBook);
                  } else { // sellAmount 보다 매수 요청량이 적을 때
                    sellOrderBook.updateAmount(restAmount);

                    OrderBook completedOrderBook = OrderBook.builder()
                        .userId(sellOrderBook.getUserId())
                        .coinId(sellOrderBook.getCoinId())
                        .price(sellOrderBook.getPrice())
                        .amount(amountToBuy.get())
                        .type(sellOrderBook.getType())
                        .status(OrderStatus.COMPLETED.name())
                        .createdAt(sellOrderBook.getCreatedAt())
                        .build();

                    amountToBuy.set(0L);

                    return Mono.when(
                        orderBookRepository.save(sellOrderBook),
                        orderBookRepository.save(completedOrderBook)
                    );
                  }
                } else {
                  // "sellAmount 보다 매수량이 많은 부분"
                  amountToBuy.getAndUpdate(current -> current - sellAmount);
                  sellOrderBook.updateStatusToCompleted();

                  return orderBookRepository.save(sellOrderBook);
                }
              }));
        })
        .onErrorMap(e -> new OrderBookSaveException("매수 요청 처리 중 에러 발생: " + e.getMessage()))
        .then(Mono.defer(() -> { // 모든 매도 주문 처리가 끝난 후
          long restAmountToBuy = amountToBuy.get();
          long completedAmountToBuy = amount - restAmountToBuy;
          LocalDateTime nowTime = LocalDateTime.now();

          // TODO: 2025-02-26 현재가 수정 (카프카를 통해 넘겨주는 건 너무 오바인듯), 일단 명시 
          if (restAmountToBuy < amount) {
            coinWebSocketHandler.updateCurrentPrice(coinId, price);
          }

          // amount = 4, amountToBuy = 1 -> 3개는 completed, 1개는 pending 이어야 함
          // 매수한 게 없으면 그대로 PENDING 저장
          if (restAmountToBuy == amount) {
            OrderBook pendingOrderBook = OrderBook.builder()
                .userId(userId)
                .coinId(coinId)
                .type(OrderType.BUY.name())
                .amount(restAmountToBuy)
                .price(price)
                .status(OrderStatus.PENDING.name())
                .createdAt(nowTime)
                .build();
            return orderBookRepository.save(pendingOrderBook)
                .thenReturn(new Message("매수 요청이 완료되었습니다. 주문이 PENDING 상태로 등록되었습니다."));
          }

          // 일부만 체결 된 거면
          if (amount > restAmountToBuy && restAmountToBuy > 0) {

            OrderBook pendingOrderBook = OrderBook.builder()
                .userId(userId)
                .coinId(coinId)
                .type(OrderType.BUY.name())
                .amount(restAmountToBuy) // 남은 건 펜딩
                .price(price)
                .status(OrderStatus.PENDING.name())
                .createdAt(nowTime)
                .build();

            OrderBook completedOrderBook = OrderBook.builder()
                .userId(userId)
                .coinId(coinId)
                .type(OrderType.BUY.name())
                .amount(completedAmountToBuy) // 체결된 건 컴플리티드
                .price(price)
                .status(OrderStatus.COMPLETED.name())
                .createdAt(nowTime)
                .build();

            return Mono.when(
                orderBookRepository.save(pendingOrderBook),
                orderBookRepository.save(completedOrderBook)
            ).thenReturn(new Message("매수 요청이 완료되었습니다. 일부 주문이 PENDING 상태로 등록되었습니다."));
          }

          // 모두 체결 됐을 때
          OrderBook completedOrderBook = OrderBook.builder()
              .userId(userId)
              .coinId(coinId)
              .type(OrderType.BUY.name())
              .amount(completedAmountToBuy)
              .price(price)
              .status(OrderStatus.COMPLETED.name())
              .createdAt(nowTime)
              .build();

          return orderBookRepository
              .save(completedOrderBook)
              .thenReturn(new Message("매수 요청이 완료되었습니다. 모든 주문이 체결되었습니다."));
        }))
        .onErrorMap(e -> new OrderBookSaveException("남은 매수 요청 처리 중 오류 발생: " + e.getMessage()))
        .as(transactionalOperator::transactional); // 트랜잭션 적용;
  }

  public Flux<OrderBook> findPendingOrders(Long coinId, Long price, String type) {

    return orderBookRepository.findByCoinIdAndPriceAndTypeAndStatusOrderByCreatedAtAsc(
            coinId, price, type, OrderStatus.PENDING.name()
        )
        .onErrorMap(e -> new FindOrderBookDataException("매수할 OrderBook 조회 실패: " + e.getMessage()));
  }

  public Mono<OrderBook> findCompletedOrdersFromSameRequestByStatus(OrderBook orderBook,
      String requestStatus) {
    String userId = orderBook.getUserId();
    Long coinId = orderBook.getCoinId();
    Long price = orderBook.getPrice();
    String type = orderBook.getType();
    LocalDateTime createdAt = orderBook.getCreatedAt();

    return orderBookRepository.findByUserIdAndCoinIdAndPriceAndTypeAndStatusAndCreatedAt(
            userId, coinId, price, type, requestStatus, createdAt
        )
        .onErrorMap(e -> new FindOrderBookDataException("체결된 OrderBook 조회 실패: " + e.getMessage()));
  }

  // 매도 메서드
  public Mono<Message> sellCoin(String userId, TradeOrder tradeOrder) {

    Long coinId = tradeOrder.getCoinId();
    Long price = tradeOrder.getPrice();
    Long amount = tradeOrder.getAmount();

    AtomicReference<Long> amountToSell = new AtomicReference<>(amount);

    // 매수 요청 데이터를 조회
    return findPendingOrders(coinId, price, OrderType.BUY.name())
        // 조회되는 데이터가 있으면 아래 메서드 실행
        .concatMap(buyOrderBook -> {
          if (amountToSell.get() == 0) {
            return Mono.empty();
          }

          return findCompletedOrdersFromSameRequestByStatus(buyOrderBook,
              OrderStatus.COMPLETED.name())
              .flatMap(completedOrderBook -> { // 체결된 주문이 존재하는 경우
                long buyAmount = buyOrderBook.getAmount();
                long completedBuyAmount = completedOrderBook.getAmount();

                // 요청한 매도량보다 매수량이 이상일 경우
                if (amountToSell.get() <= buyAmount) {
                  long restAmount = buyAmount - amountToSell.get();
                  amountToSell.set(0L); // 어차피 0개가 됨

                  // 딱 떨어지는 경우
                  if (restAmount == 0) {
                    completedOrderBook.updateAmount(buyAmount + completedBuyAmount);

                    return orderBookRepository.delete(buyOrderBook)
                        .then(orderBookRepository.save(completedOrderBook));
                  }

                  // 요청 매수량을 제해도 남는 경우
                  completedOrderBook.updateAmount(completedBuyAmount + amountToSell.get());
                  buyOrderBook.updateAmount(restAmount);

                  return orderBookRepository.save(completedOrderBook)
                      .then(orderBookRepository.save(buyOrderBook));
                }

                // 요청한 매도량이 매수량보다 클 경우
                amountToSell.getAndUpdate(current -> current - buyAmount);
                completedOrderBook.updateAmount(completedBuyAmount + buyAmount);

                return orderBookRepository.delete(buyOrderBook)
                    .then(orderBookRepository.save(completedOrderBook));

              })
              .switchIfEmpty(Mono.defer(() -> { // 체결된 주문이 조회되지 않을 때 처리
                long buyAmount = buyOrderBook.getAmount();

                // 요청한 매도량보다 매수량이 크거나 같을 때
                if (amountToSell.get() <= buyAmount) {
                  long restAmount = buyAmount - amountToSell.get();
                  long currentAmountToSell = amountToSell.get();
                  amountToSell.set(0L);

                  // 딱 떨어질 떼
                  if (restAmount == 0) {
                    buyOrderBook.updateStatusToCompleted();

                    return orderBookRepository.save(buyOrderBook);
                  }

                  buyOrderBook.updateAmount(restAmount);

                  OrderBook completedBuyOrderBook = OrderBook.builder()
                      .userId(buyOrderBook.getUserId())
                      .coinId(buyOrderBook.getCoinId())
                      .price(buyOrderBook.getPrice())
                      .amount(currentAmountToSell)
                      .type(buyOrderBook.getType())
                      .status(OrderStatus.COMPLETED.name())
                      .createdAt(buyOrderBook.getCreatedAt())
                      .build();

                  return orderBookRepository.save(buyOrderBook)
                      .then(orderBookRepository.save(completedBuyOrderBook));
                }

                // 요청한 매도량이 더 클 경우
                amountToSell.getAndUpdate(current -> current - buyAmount);
                buyOrderBook.updateStatusToCompleted();

                return orderBookRepository.save(buyOrderBook);
              }))
              .onErrorMap(e -> new OrderBookSaveException("매도 요청 처리 중 오류 발생: " + e.getMessage()));

        })
        .then(Mono.defer(() -> { // 모든 매도 매수 계산이 끝난 후
          long restAmountToSell = amountToSell.get();
          long completedAmountToSell = amount - restAmountToSell;
          LocalDateTime nowTime = LocalDateTime.now();

          OrderBook pendingOrderBook = OrderBook.builder()
              .userId(userId)
              .coinId(coinId)
              .type(OrderType.SELL.name())
              .amount(restAmountToSell)
              .price(price)
              .status(OrderStatus.PENDING.name())
              .createdAt(nowTime)
              .build();

          OrderBook completedOrderBook = OrderBook.builder()
              .userId(userId)
              .coinId(coinId)
              .type(OrderType.SELL.name())
              .amount(completedAmountToSell)
              .price(price)
              .status(OrderStatus.COMPLETED.name())
              .createdAt(nowTime)
              .build();

          // 매도한 게 없으면 그대로 PENDING 저장
          if (completedAmountToSell == 0) {
            return orderBookRepository.save(pendingOrderBook)
                .thenReturn(new Message("매도 요청이 완료되었습니다. 주문이 PENDING 상태로 등록되었습니다."));
          }

          // 일부만 체결된 거면
          if (amount > restAmountToSell && restAmountToSell > 0) {
//            coinWebSocketHandler.updateCurrentPrice(coinId, price);

            return orderBookRepository.save(pendingOrderBook)
                .then(orderBookRepository.save(completedOrderBook))
                .thenReturn(new Message("매도 요청이 완료되었습니다. 일부 주문이 PENDING 상태로 등록되었습니다."));
          }

          // 모두 체결 됐을 때
          completedOrderBook.setUpdatedAt(nowTime);
//          coinWebSocketHandler.updateCurrentPrice(coinId, price);

          return orderBookRepository.save(completedOrderBook)
              .thenReturn(new Message("매도 요청이 완료되었습니다. 모든 주문이 체결되었습니다."));

        }))
        .onErrorMap(e -> new OrderBookSaveException("남은 매도 요청 처리 중 오류 발생: " + e.getMessage()))
        .as(transactionalOperator::transactional)
        .flatMap(result -> {
          if (amountToSell.get() < amount) return coinWebSocketHandler.updateCurrentPrice(coinId, price).thenReturn(result);
          return Mono.just(result);
        });
  }
}
