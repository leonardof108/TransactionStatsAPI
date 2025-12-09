package com.leonardof108.TransactionStatsAPI.api.service;

import com.leonardof108.TransactionStatsAPI.api.dto.EstatisticaResponse;
import com.leonardof108.TransactionStatsAPI.api.dto.TransacaoRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class TransacaoService {

    private final ConcurrentNavigableMap<OffsetDateTime, BigDecimal> transacoes = new ConcurrentSkipListMap<>();

    public void adicionarTransacao(TransacaoRequest transacao) {
        transacoes.put(transacao.dataHora(), transacao.valor());
    }

    public void limparTransacoes() {
        transacoes.clear();
    }

    public EstatisticaResponse calcularEstatisticas(int intervaloSegundos) {
        OffsetDateTime agora = OffsetDateTime.now();
        OffsetDateTime limite = agora.minusSeconds(intervaloSegundos);

        ConcurrentNavigableMap<OffsetDateTime, BigDecimal> transacoesRecentes = transacoes.subMap(limite, true, agora, true);

        if (transacoesRecentes.isEmpty()) {
            return new EstatisticaResponse(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        long count = transacoesRecentes.size();
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal min = null;
        BigDecimal max = null;

        for (BigDecimal valor : transacoesRecentes.values()) {
            sum = sum.add(valor);
            if (min == null || valor.compareTo(min) < 0) {
                min = valor;
            }
            if (max == null || valor.compareTo(max) > 0) {
                max = valor;
            }
        }

        BigDecimal avg = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        return new EstatisticaResponse(count, sum, avg, min, max);
    }
}
