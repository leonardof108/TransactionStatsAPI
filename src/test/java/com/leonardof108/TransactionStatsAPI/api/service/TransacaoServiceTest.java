package com.leonardof108.TransactionStatsAPI.api.service;

import com.leonardof108.TransactionStatsAPI.api.dto.EstatisticaResponse;
import com.leonardof108.TransactionStatsAPI.api.dto.TransacaoRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

// This is a pure unit test, so no Spring annotations are needed.
class TransacaoServiceTest {

    @Test
    void deveRetornarEstatisticasVaziasApos61Segundos() {
        // Arrange
        Instant initialInstant = Instant.parse("2024-07-30T10:00:00Z");
        Clock clock = Clock.fixed(initialInstant, ZoneOffset.UTC);
        TransacaoService transacaoService = new TransacaoService(clock);

        transacaoService.adicionarTransacao(new TransacaoRequest(new BigDecimal("100.00"), OffsetDateTime.now(clock)));

        // Act: Advance the clock by 61 seconds
        Instant futureInstant = initialInstant.plusSeconds(61);
        Clock futureClock = Clock.fixed(futureInstant, ZoneOffset.UTC);
        
        // Create a new service instance at the future time
        TransacaoService serviceAtFutureTime = new TransacaoService(futureClock);
        // Manually copy the buckets to simulate the state of the service at that future time
        System.arraycopy(transacaoService.getBucketsForTest(), 0, serviceAtFutureTime.getBucketsForTest(), 0, 60);

        EstatisticaResponse response = serviceAtFutureTime.calcularEstatisticas(60);

        // Assert
        assertEquals(0, response.count());
        assertEquals(BigDecimal.ZERO, response.sum());
    }

    @Test
    void deveSomarCorretamenteMultiplasTransacoesNoMesmoSegundo() {
        // Arrange
        Clock clock = Clock.fixed(Instant.parse("2024-07-30T10:00:30Z"), ZoneOffset.UTC);
        TransacaoService transacaoService = new TransacaoService(clock);
        
        OffsetDateTime timestamp = OffsetDateTime.now(clock);
        transacaoService.adicionarTransacao(new TransacaoRequest(new BigDecimal("10.00"), timestamp));
        transacaoService.adicionarTransacao(new TransacaoRequest(new BigDecimal("20.50"), timestamp));
        transacaoService.adicionarTransacao(new TransacaoRequest(new BigDecimal("30.00"), timestamp));

        // Act
        EstatisticaResponse response = transacaoService.calcularEstatisticas(60);

        // Assert
        assertEquals(3, response.count());
        assertEquals(new BigDecimal("60.50"), response.sum());
    }

    @Test
    void deveLidarComTransacoesNaFronteiraDosSegundos59e00() {
        // Arrange
        Instant boundaryInstant = Instant.parse("2024-07-30T10:00:59Z");
        Clock clock = Clock.fixed(boundaryInstant, ZoneOffset.UTC);
        TransacaoService transacaoService = new TransacaoService(clock);

        OffsetDateTime timeAt59 = OffsetDateTime.now(clock);
        OffsetDateTime timeAt00 = timeAt59.plusSeconds(1);

        transacaoService.adicionarTransacao(new TransacaoRequest(new BigDecimal("59.00"), timeAt59));
        transacaoService.adicionarTransacao(new TransacaoRequest(new BigDecimal("1.00"), timeAt00));

        // Act: Check the stats from the perspective of the latest transaction time
        Clock futureClock = Clock.fixed(timeAt00.toInstant(), ZoneOffset.UTC);
        TransacaoService serviceAtFutureTime = new TransacaoService(futureClock);
        System.arraycopy(transacaoService.getBucketsForTest(), 0, serviceAtFutureTime.getBucketsForTest(), 0, 60);

        EstatisticaResponse response = serviceAtFutureTime.calcularEstatisticas(60);

        // Assert
        assertEquals(2, response.count());
        assertEquals(new BigDecimal("60.00"), response.sum());
    }
}
