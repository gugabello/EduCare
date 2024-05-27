package com.example.application.services;

import com.example.application.data.Atendimentos;
import com.example.application.data.AtendimentosRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class AtendimentosService {

    private final AtendimentosRepository repository;

    public AtendimentosService(AtendimentosRepository repository) {
        this.repository = repository;
    }

    public Optional<Atendimentos> get(Long id) {
        return repository.findById(id);
    }

    public Atendimentos update(Atendimentos entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Atendimentos> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Atendimentos> list(Pageable pageable, Specification<Atendimentos> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
