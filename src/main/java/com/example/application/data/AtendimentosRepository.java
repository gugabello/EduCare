package com.example.application.data;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AtendimentosRepository
        extends
            JpaRepository<Atendimentos, Long>,
            JpaSpecificationExecutor<Atendimentos> {

}
