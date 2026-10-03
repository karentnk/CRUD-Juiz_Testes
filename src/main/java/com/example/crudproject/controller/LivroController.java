package com.example.crudproject.controller;

import com.example.crudproject.model.Livro;
import com.example.crudproject.model.LivroIn;
import com.example.crudproject.service.LivroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class LivroController {

    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Livro>> listar(
            @RequestParam(required = false) Boolean disponivel) {
        return ResponseEntity.ok(service.listar(disponivel));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> obter(@PathVariable long id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @PostMapping
    public ResponseEntity<Livro> criar(@RequestBody LivroIn body) {
        return ResponseEntity.status(201).body(service.criar(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(
            @PathVariable long id,
            @RequestBody(required = false) LivroIn body) {
        return ResponseEntity.ok(service.atualizar(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable long id) {
        service.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/emprestar")
    public ResponseEntity<Livro> emprestar(@PathVariable long id) {
        return ResponseEntity.ok(service.emprestar(id));
    }

    @PostMapping("/{id}/devolver")
    public ResponseEntity<Livro> devolver(@PathVariable long id) {
        return ResponseEntity.ok(service.devolver(id));
    }
}
