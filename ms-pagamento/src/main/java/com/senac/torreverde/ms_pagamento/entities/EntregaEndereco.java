package com.senac.torreverde.ms_pagamento.entities;

import jakarta.persistence.*;

// Entidade EntregaEndereco
@Entity
public class EntregaEndereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entrega_endereco_id")
    private Integer id;

    @Column(name = "entrega_endereco_endereco", length = 300)
    private String endereco;

    @Column(name = "entrega_endereco_cep", length = 7)
    private String cep;

    @OneToOne
    @JoinColumn(name = "registro_pagamento_id")
    private RegistroPagamento registroPagamentoId;

    public EntregaEndereco() {
    }

    public EntregaEndereco(Integer id, String endereco, String cep, RegistroPagamento registroPagamentoId) {
        this.id = id;
        this.endereco = endereco;
        this.cep = cep;
        this.registroPagamentoId = registroPagamentoId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public RegistroPagamento getRegistroPagamentoId() {
        return registroPagamentoId;
    }

    public void setRegistroPagamentoId(RegistroPagamento registroPagamentoId) {
        this.registroPagamentoId = registroPagamentoId;
    }
}
