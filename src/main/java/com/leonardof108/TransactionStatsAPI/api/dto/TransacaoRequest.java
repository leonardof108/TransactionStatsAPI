package com.leonardof108.TransactionStatsAPI.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


// record that represents the json payload
// strict json format, no future dates, non-negative values
public record TransacaoRequest(
        @NotNull(message = "Valor is mandatory")
        @DecimalMin(value = "0.0", message = "Valor cannot be negative")
        BigDecimal valor,

        @NotNull(message = "DataHora is mandatory")
        @PastOrPresent(message = "DataHora cannot be in the future")
        OffsetDateTime dataHora
) {}
