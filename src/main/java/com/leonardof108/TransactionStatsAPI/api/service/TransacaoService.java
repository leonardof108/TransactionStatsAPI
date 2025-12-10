package com.leonardof108.TransactionStatsAPI.api.service;

import com.leonardof108.TransactionStatsAPI.api.dto.EstatisticaResponse;
import com.leonardof108.TransactionStatsAPI.api.dto.TransacaoRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TransacaoService {

    private final List<TransacaoRequest> transacoes = Collections.synchronizedList(new ArrayList<>());

    public void adicionarTransacao(TransacaoRequest transacao) {
        transacoes.add(transacao);
    }

    public void limparTransacoes() {
        transacoes.clear();
    }

    public EstatisticaResponse calcularEstatisticas(int intervaloSegundos) {
        OffsetDateTime agora = OffsetDateTime.now();
        OffsetDateTime limite = agora.minusSeconds(intervaloSegundos);

        List<TransacaoRequest> copiaTransacoes;
        synchronized (transacoes) {
            copiaTransacoes = new ArrayList<>(transacoes);
        }

        List<BigDecimal> valoresRecentes = copiaTransacoes.stream()
                .filter(t -> t.dataHora().isAfter(limite) && !t.dataHora().isAfter(agora))
                .map(TransacaoRequest::valor)
                .toList();

        if (valoresRecentes.isEmpty()) {
            return new EstatisticaResponse(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        long count = valoresRecentes.size();

        BigDecimal sum = valoresRecentes.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal min = valoresRecentes.stream()
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal max = valoresRecentes.stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal avg = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        return new EstatisticaResponse(count, sum, avg, min, max);
    }
}