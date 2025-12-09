package com.leonardof108.TransactionStatsAPI.api.dto;

import java.math.BigDecimal;

public record EstatisticaResponse(
        long count,
        BigDecimal sum,
        BigDecimal avg,
        BigDecimal min,
        BigDecimal max
) {}
