/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.invest.factory;

import br.gov.es.invest.dto.objeto.ObjetoCadastroFormDto;
import br.gov.es.invest.model.Custo;
import br.gov.es.invest.model.IndicadaPor;
import br.gov.es.invest.service.CustoService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 *
 * @author gean.carneiro
 */
@Component
@RequiredArgsConstructor
public class CustoFactory {
    
    private final CustoService custoSrv;
    private final FonteFactory fonteFactory;
    
    public Custo fromDto(ObjetoCadastroFormDto.Custo dto, Long objetoId) {
        
        Optional<Custo> optCusto = this.custoSrv.findByAnoExercicio(dto.ano(), objetoId);
        Custo custo = optCusto.orElseGet(() -> Custo.builder().anoExercicio(dto.ano()).indicadaPor(new HashSet<>()).build());
        
        
        Set<IndicadaPor> setIndicadaPor = new HashSet<>();
            
        for(ObjetoCadastroFormDto.ValoresFonte vf : dto.valoresFontes()){
            List<IndicadaPor> ips = custo.getIndicadaPor().stream()
                    .filter(ipm -> ipm.getFonteOrcamentaria().getCodigo().equals(vf.fonte().getCodigo()))
                    .collect(Collectors.toList());


            IndicadaPor ip;
            if(!ips.isEmpty()) {
                ip = ips.get(0);

            } else {
                ip = new IndicadaPor();

                ip.setFonteOrcamentaria(fonteFactory.fromDto(vf.fonte()));
                ip.setGnd(4);

            }

            if(vf.contratado() != null) ip.setContratado(vf.contratado());
            ip.setPrevisto(vf.previsto());


            setIndicadaPor.add(ip);

        }

        custo.setIndicadaPor(setIndicadaPor);
        
        return custo;
    }
    
}
