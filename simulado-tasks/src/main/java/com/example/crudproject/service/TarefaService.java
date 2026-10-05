package com.example.crudproject.service;

import com.example.crudproject.model.Tarefa;
import com.example.crudproject.model.TarefaIn;
import com.example.crudproject.repository.TarefaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TarefaService {

    private final TarefaRepository repository;

    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }

    private void exigir(boolean condicao) {
        if (!condicao) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public Tarefa criar(TarefaIn in) {
        exigir(in.titulo() != null && !in.titulo().isBlank());
        Tarefa t = new Tarefa();
        t.setTitulo(in.titulo());
        t.setDescricao(in.descricao() == null ? "" : in.descricao());
        t.setConcluida(false);
        return repository.save(t);
    }

    public List<Tarefa> listar() {
        return repository.findAll();
    }

    public Tarefa obter(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Tarefa atualizar(long id, TarefaIn in) {
        Tarefa t = obter(id);
        if (in == null) return repository.save(t);
        if (in.titulo() != null) {
            exigir(!in.titulo().isBlank());
            t.setTitulo(in.titulo());
        }
        if (in.descricao() != null) t.setDescricao(in.descricao());
        if (in.concluida() != null) t.setConcluida(in.concluida());
        return repository.save(t);
    }

    public void remover(long id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        repository.deleteById(id);
    }
}
