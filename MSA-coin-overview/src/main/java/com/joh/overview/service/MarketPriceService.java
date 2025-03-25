package com.joh.overview.service;

import com.joh.common.coin.CoinRepository;
import com.joh.common.coin.projection.IdAndNameAndSymbolProjection;
import com.joh.overview.dto.CoinDTO;
import com.joh.overview.repository.MarketPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class MarketPriceService {

  private final MarketPriceRepository marketPriceRepository;
  private final CoinRepository coinRepository;

  // 홈에서 표현할 기본 코인 데이터 (initial 메서드)
  public Flux<CoinDTO> getCoinsForInitial() {
    Flux<IdAndNameAndSymbolProjection> coins = coinRepository.findCoinsSummaryProjection();

    return coins.flatMap(coin ->
        marketPriceRepository
            .findLatestCurrentPriceByCoinId(coin.getId())
            .map(currentPriceProjection -> {
              Long currentPrice = currentPriceProjection.getCurrentPrice();

              return CoinDTO.builder()
                  .id(coin.getId())
                  .name(coin.getName())
                  .symbol(coin.getSymbol())
                  .initialPrice(currentPrice) // 처음엔 현재가로!
                  .currentPrice(currentPrice)
                  .build();
            })
    );
  }

  /*
   * 00시 초기화
   *
   * 배치 필요 - 1시간마다 현재 코인에 대한 데이터를 저장해야 함
   * ※ 00시에 초기화가 되는데, 1시간마다 배치를 돌리면 겹치게 된다. 이걸 어떻게 처리할 것인지 고민 必
   *
   * 첫 초기화 데이터 필요
   * 1. MarketPrice 보내야 함
   * 2.
   * */
}
