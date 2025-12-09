package com.leonardof108.TransactionStatsAPI.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;


// record that represents the json payload
// strict json format, no future dates, non-negative values
public record TransacaoRequest(
        @NotNull(message = "Valor is mandatory")
        @Min(value = 0, message = "Valor cannot be negative")
        Double valor,

        @NotNull(message = "DataHora is mandatory")
        OffsetDateTime dataHora
) {}