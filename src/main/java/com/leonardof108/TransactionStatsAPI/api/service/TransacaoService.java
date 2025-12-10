package com.leonardof108.TransactionStatsAPI.api.service;

import com.leonardof108.TransactionStatsAPI.api.dto.EstatisticaResponse;
import com.leonardof108.TransactionStatsAPI.api.dto.TransacaoRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

@Service
public class TransacaoService {

    // 60 buckets, one for each second of the minute
    private final Bucket[] buckets = new Bucket[60];

    public TransacaoService() {
        // Initialize empty buckets to avoid null checks later
        for (int i = 0; i < 60; i++) {
            buckets[i] = new Bucket();
        }
    }

    public void adicionarTransacao(TransacaoRequest transacao) {
        OffsetDateTime dataHora = transacao.dataHora();
        OffsetDateTime now = OffsetDateTime.now();

        // The validation for future dates is handled by annotations on the DTO.
        // This check provides an O(1) optimization to ignore old transactions immediately.
        if (dataHora.isBefore(now.minusSeconds(60))) {
            return;
        }

        long timestamp = dataHora.toEpochSecond();
        int index = (int) (timestamp % 60); // Calculates the "slot" (0 to 59)

        Bucket bucket = buckets[index];
        
        // Thread-safe update of the bucket
        synchronized (bucket) {
            // If the bucket holds data from a previous minute (based on its timestamp), reset it.
            if (bucket.timestamp != timestamp) {
                bucket.reset(timestamp);
            }
            bucket.add(transacao.valor());
        }
    }

    public void limparTransacoes() {
        for (Bucket bucket : buckets) {
            synchronized (bucket) {
                bucket.reset(0); // Resetting to a '0' timestamp effectively clears it.
            }
        }
    }

    public EstatisticaResponse calcularEstatisticas(int intervaloSegundos) {
        long count = 0;
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal min = null;
        BigDecimal max = null;

        long currentTime = OffsetDateTime.now().toEpochSecond();

        // This loop is always 60 iterations, ensuring O(1) complexity.
        for (Bucket bucket : buckets) {
            synchronized (bucket) {
                // Only include buckets that are within the current 60-second sliding window.
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
     * Inner class to hold aggregated statistics for a single second.
     */
    private static class Bucket {
        public long timestamp; // The epoch second this bucket represents.
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
