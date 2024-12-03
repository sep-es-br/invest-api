package br.gov.es.invest.dto.acessocidadaoapi;

import java.util.List;

public record UnidadesACResponseDto(
    String guid,
    String cnpj,
    String filial,
    String razaoSocial,
    String nomeFantasia,
    String sigla,
    List<Object> contatos,
    List<String> emails,
    Endereco endereco,
    Esfera esfera,
    Poder poder,
    OrganizacaoPai organizacaoPai,
    List<String> sites
) {
    
}

record Esfera(String descricao) {
}

record Poder(String descricao){}

record OrganizacaoPai(String guid, String razaoSocial, String sigla){}

record Endereco(
    String logradouro,
    String complemento,
    String bairro,
    String cep,
    Municipio municipio
){}

record Municipio(
    String guid,
    Integer codigoIbge,
    String nome,
    String uf
) {
}