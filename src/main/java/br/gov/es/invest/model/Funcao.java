package br.gov.es.invest.model;

import java.util.Set;

import org.springframework.data.neo4j.core.schema.Node;

import br.gov.es.invest.dto.FuncaoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Node
public class Funcao extends Entidade {
    
    private String nome;

    public Funcao(String nome){
        this.nome = nome;
    }

    public Funcao(FuncaoDTO dto) {
        this.setId(dto.getId());
        this.nome = dto.getNome();
    }

    public static boolean testarFuncao(Set<Funcao> funcoes, String targetFuncao){
        
        for(Funcao funcao : funcoes){
            if(funcao.getNome().equals(targetFuncao)) return true;
        }

        return false;
    }

}
