package com.leonardof108.TransactionStatsAPI.api.service;

import com.leonardof108.TransactionStatsAPI.api.dto.EstatisticaResponse;
import com.leonardof108.TransactionStatsAPI.api.dto.TransacaoRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.OffsetDateTime;

@Service
public class TransacaoService {

    private final Clock clock;
    private final Bucket[] buckets = new Bucket[60];

    public TransacaoService(Clock clock) {
        this.clock = clock;
        for (int i = 0; i < 60; i++) {
            buckets[i] = new Bucket();
        }
    }

    public void adicionarTransacao(TransacaoRequest transacao) {
        OffsetDateTime dataHora = transacao.dataHora();
        OffsetDateTime now = OffsetDateTime.now(clock);

        if (dataHora.isBefore(now.minusSeconds(60))) {
            return;
        }

        long timestamp = dataHora.toEpochSecond();
        int index = (int) (timestamp % 60);

        Bucket bucket = buckets[index];
        
        synchronized (bucket) {
            if (bucket.timestamp != timestamp) {
                bucket.reset(timestamp);
            }
            bucket.add(transacao.valor());
        }
    }

    public void limparTransacoes() {
        for (Bucket bucket : buckets) {
            synchronized (bucket) {
                bucket.reset(0);
            }
        }
    }

    public EstatisticaResponse calcularEstatisticas(int intervaloSegundos) {
        long count = 0;
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal min = null;
        BigDecimal max = null;

        long currentTime = OffsetDateTime.now(clock).toEpochSecond();

        for (Bucket bucket : buckets) {
            synchronized (bucket) {
                if (bucket.timestamp > 0 && currentTime - bucket.timestamp < 60) {
                    count += bucket.count;
                    sum = sum.add(bucket.sum);
                    
                    if (min == null || bucket.min.compareTo(min) < 0) min = bucket.min;
                    if (max == null || bucket.max.compareTo(max) > 0) max = bucket.max;
                }
            }
        }

        if (count == 0) {
            return new EstatisticaResponse(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        BigDecimal avg = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        return new EstatisticaResponse(count, sum, avg, min != null ? min : BigDecimal.ZERO, max != null ? max : BigDecimal.ZERO);
    }

    /**
     * This method is package-private and is intended only for testing purposes.
     */
    Bucket[] getBucketsForTest() {
        return buckets;
    }

    static class Bucket {
        public long timestamp;
        public BigDecimal sum = BigDecimal.ZERO;
        public BigDecimal min = null;
        public BigDecimal max = null;
        public long count = 0;

        public void add(BigDecimal valor) {
            sum = sum.add(valor);
            count++;
            
            if (min == null || valor.compareTo(min) < 0) min = valor;
            if (max == null || valor.compareTo(max) > 0) max = valor;
        }

        public void reset(long newTimestamp) {
            timestamp = newTimestamp;
            sum = BigDecimal.ZERO;
            min = null;
            max = null;
            count = 0;
        }
    }
}
