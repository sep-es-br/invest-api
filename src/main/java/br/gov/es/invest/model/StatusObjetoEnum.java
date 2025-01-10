package br.gov.es.invest.model;

import lombok.Data;
import lombok.Getter;

@Getter
public enum StatusObjetoEnum {
    EM_APROVACAO("Em Aprovação"),
    CADASTRADO("Cadastrado");

    private String nome;

    StatusObjetoEnum(String nome){
        this.nome = nome;
    }
}
