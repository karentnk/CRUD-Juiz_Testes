package com.example.crudproject.controller;

import com.example.crudproject.model.Tarefa;
import com.example.crudproject.model.TarefaIn;
import com.example.crudproject.service.TarefaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TarefaController {

    private final TarefaService service;

    public TarefaController(TarefaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Tarefa>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarefa> obter(@PathVariable long id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @PostMapping
    public ResponseEntity<Tarefa> criar(@RequestBody TarefaIn body) {
        return ResponseEntity.status(201).body(service.criar(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tarefa> atualizar(
            @PathVariable long id,
            @RequestBody(required = false) TarefaIn body) {
        return ResponseEntity.ok(service.atualizar(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }
}
