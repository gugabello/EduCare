package com.example.application.data;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;

@Entity
public class Atendimentos extends AbstractEntity {

    private String nome;
    private String periodo;
    private String curso;
    private LocalDate dataDaUltimaConsulta;
    @Email
    private String email;
    private String telefone;
    private String observacoes;
    private String links;

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getPeriodo() {
        return periodo;
    }
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
    public String getCurso() {
        return curso;
    }
    public void setCurso(String curso) {
        this.curso = curso;
    }
    public LocalDate getDataDaUltimaConsulta() {
        return dataDaUltimaConsulta;
    }
    public void setDataDaUltimaConsulta(LocalDate dataDaUltimaConsulta) {
        this.dataDaUltimaConsulta = dataDaUltimaConsulta;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    public String getObservacoes() {
        return observacoes;
    }
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    public String getLinks() {
        return links;
    }
    public void setLinks(String links) {
        this.links = links;
    }

}
