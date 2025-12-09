package com.leonardof108.TransactionStatsAPI.api.controller;

import com.leonardof108.TransactionStatsAPI.api.dto.EstatisticaResponse;
import com.leonardof108.TransactionStatsAPI.api.service.TransacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/estatistica")
public class EstatisticaController {

    private final TransacaoService service;

    public EstatisticaController(TransacaoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<EstatisticaResponse> getEstatisticas() {
        return ResponseEntity.ok(service.calcularEstatisticas(60));
    }
}