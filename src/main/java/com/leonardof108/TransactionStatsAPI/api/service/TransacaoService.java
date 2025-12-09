package com.leonardof108.TransactionStatsAPI.api.service;

import com.leonardof108.TransactionStatsAPI.api.dto.EstatisticaResponse;
import com.leonardof108.TransactionStatsAPI.api.dto.TransacaoRequest;import com.leonardof108.TransactionStatsAPI.api.exception.UnprocessableEntityException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;


// we'll store everything in a thread-safe list
@Service
public class TransacaoService {

    // Thread-safe list to replace DB
    private final List<TransacaoRequest> transacoes = Collections.synchronizedList(new ArrayList<>());

    public void adicionarTransacao(TransacaoRequest transacao) {
        // Validation - Transaction must not be in the future
        if (transacao.dataHora().isAfter(OffsetDateTime.now())) {
            throw new UnprocessableEntityException();
        }

        // Validation - Transaction must happen in the past

        transacoes.add(transacao);
    }

    public List<TransacaoRequest> buscarTransacoes() {
        return transacoes;
    }

    public void limparTransacoes() {
        transacoes.clear();
    }

    public EstatisticaResponse calcularEstatisticas(int intervaloSegundos) {
        OffsetDateTime corteTempo = OffsetDateTime.now().minusSeconds(intervaloSegundos);

        // Synchronized block to ensure thread safety during stream processing
        synchronized (transacoes) {
            DoubleSummaryStatistics stats = transacoes.stream()
                    .filter(t -> t.dataHora().isAfter(corteTempo)) // Filter last 60s
                    .mapToDouble(TransacaoRequest::valor)
                    .summaryStatistics();

            if (stats.getCount() == 0) {
                return new EstatisticaResponse(0, 0.0, 0.0, 0.0, 0.0);
            }

            return new EstatisticaResponse(
                    stats.getCount(),
                    stats.getSum(),
                    stats.getAverage(),
                    stats.getMin(),
                    stats.getMax()
            );
        }
    }
}


