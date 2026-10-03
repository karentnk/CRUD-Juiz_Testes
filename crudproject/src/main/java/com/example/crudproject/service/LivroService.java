package com.example.crudproject.service;

import com.example.crudproject.model.Livro;
import com.example.crudproject.model.LivroIn;
import com.example.crudproject.repository.LivroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.List;

@Service
public class LivroService {

    private static final int ANO_MINIMO = 1450;

    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    private void exigir(boolean condicao) {
        if (!condicao) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    private void validarCampos(String titulo, String autor, Integer ano, Integer paginas) {
        exigir(titulo != null && !titulo.isBlank());
        exigir(autor != null && !autor.isBlank());
        exigir(ano != null && ano >= ANO_MINIMO && ano <= Year.now().getValue());
        exigir(paginas == null || paginas >= 1);
    }

    public Livro criar(LivroIn in) {
        validarCampos(in.titulo(), in.autor(), in.ano(), in.paginas());
        Livro l = new Livro();
        l.setTitulo(in.titulo());
        l.setAutor(in.autor());
        l.setAno(in.ano());
        l.setPaginas(in.paginas() == null ? 0 : in.paginas());
        l.setDisponivel(true);
        return repository.save(l);
    }


    public List<Livro> listar(Boolean disponivel) {
        if (disponivel == null) return repository.findAll();
        return repository.findByDisponivel(disponivel);
    }

    public Livro obter(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Livro atualizar(long id, LivroIn in) {
        Livro l = obter(id);
        if (in == null) return repository.save(l);
        if (in.titulo() != null) {
            exigir(!in.titulo().isBlank());
            l.setTitulo(in.titulo());
        }
        if (in.autor() != null) {
            exigir(!in.autor().isBlank());
            l.setAutor(in.autor());
        }
        if (in.ano() != null) {
            exigir(in.ano() >= ANO_MINIMO && in.ano() <= Year.now().getValue());
            l.setAno(in.ano());
        }
        if (in.paginas() != null) {
            exigir(in.paginas() >= 1);
            l.setPaginas(in.paginas());
        }
        return repository.save(l);
    }


    public void remover(long id) {
        if (!repository.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        repository.deleteById(id);
    }


    public Livro emprestar(long id) {
        Livro l = obter(id);
        if (!l.isDisponivel()) throw new ResponseStatusException(HttpStatus.CONFLICT);
        l.setDisponivel(false);
        return repository.save(l);
    }

    public Livro devolver(long id) {
        Livro l = obter(id);
        if (l.isDisponivel()) throw new ResponseStatusException(HttpStatus.CONFLICT);
        l.setDisponivel(true);
        return repository.save(l);
    }
}
