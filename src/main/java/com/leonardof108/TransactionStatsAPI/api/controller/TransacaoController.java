package com.leonardof108.TransactionStatsAPI.api.controller;

import com.leonardof108.TransactionStatsAPI.api.dto.TransacaoRequest;
import com.leonardof108.TransactionStatsAPI.api.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> criarTransacao(@Valid @RequestBody TransacaoRequest request) {
        // Logic validations are handled in the service
        // If JSON is invalid Spring throws 400 automatically
        service.adicionarTransacao(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deletarTransacoes() {
        service.limparTransacoes();
        return ResponseEntity.ok().build();
    }
}
