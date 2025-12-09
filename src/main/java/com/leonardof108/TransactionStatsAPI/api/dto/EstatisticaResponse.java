package com.leonardof108.TransactionStatsAPI.api.dto;

public record EstatisticaResponse(
        long count,
        double sum,
        double avg,
        double min,
        double max
) {}