package br.gov.es.invest.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.Relationship.Direction;

import br.gov.es.invest.dto.ContaDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@RequiredArgsConstructor
@Node
@SuperBuilder
public class Conta extends Entidade implements Serializable {
    
    public static enum TIPO_CONTA {
        INVESTIMENTO;
        
        public static TIPO_CONTA of(String tipo){
            return TIPO_CONTA.valueOf(tipo.replace(" ", "_").toUpperCase());
        }

        @Override
        public String toString() {
            String textoOriginal = super.toString();
            
            if (textoOriginal == null || textoOriginal.isEmpty()) {
                return textoOriginal;
            }

            // 1. Converte para minúsculo e substitui _ por espaço
            String textoFormatado = textoOriginal.toLowerCase().replace("_", " ");

            // 2. Capitaliza a primeira letra de cada palavra
            StringBuilder builder = new StringBuilder();
            String[] palavras = textoFormatado.split(" ");

            for (String palavra : palavras) {
                if (!palavra.isEmpty()) {
                    // Pega a primeira letra e transforma em maiúsculo
                    builder.append(Character.toUpperCase(palavra.charAt(0)));
                    // Adiciona o resto da palavra
                    builder.append(palavra.substring(1));
                    // Adiciona um espaço entre as palavras
                    builder.append(" ");
                }
            }

            // Remove o espaço extra no final e retorna
            return builder.toString().trim();
            
        }
        
        
    }
    
    private TIPO_CONTA tipoConta;
    private String status;
    private String nome;
    private String descricao;

    @Relationship(type = "ORIENTA", direction = Direction.INCOMING)
    private PlanoOrcamentario planoOrcamentario;

    @Relationship(type = "IMPLEMENTA", direction = Direction.INCOMING)
    private UnidadeOrcamentaria unidadeOrcamentariaImplementadora;
    
    @Relationship(type = "DELIMITA", direction = Direction.INCOMING)
    private List<ExecucaoOrcamentaria> execucoesOrcamentaria;
    
    public Conta(TIPO_CONTA tipoConta) {
        this();
        this.setTipoConta(tipoConta);
    }

    public void filtrarExecucoes(Integer anoExecucao, Long fonteId) {
        if(anoExecucao != null){
            this.setExecucoesOrcamentaria(
                this.getExecucoesOrcamentaria().stream()
                .filter(exec -> exec.getAnoExercicio().equals(anoExecucao))
                .toList()
            );
        }

        if(fonteId != null) {
            for(ExecucaoOrcamentaria exec : this.getExecucoesOrcamentaria()){
                exec.setVinculadaPor(new HashSet<>(
                    exec.getVinculadaPor().stream()
                    .filter(vp -> vp.getFonteOrcamentaria().getId().equals(fonteId))
                    .collect(Collectors.toSet())
                ));
            }
        }
    }

    public static Conta parse(ContaDto dto) {
        
        if(dto == null)
            return null;
        
        Conta conta = new Conta(TIPO_CONTA.of(dto.tipoConta()));
        if(dto.unidadeOrcamentariaImplementadora() != null)
            conta.setUnidadeOrcamentariaImplementadora(new UnidadeOrcamentaria(dto.unidadeOrcamentariaImplementadora()));
        if(dto.planoOrcamentario() != null)
            conta.setPlanoOrcamentario(new PlanoOrcamentario(dto.planoOrcamentario()));

        return conta;

    }

}
