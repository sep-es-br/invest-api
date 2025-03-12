package br.gov.es.invest.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.es.invest.dto.OrgaoDto;
import br.gov.es.invest.model.Orgao;
import br.gov.es.invest.service.ACService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orgao")
@RequiredArgsConstructor
public class OrgaoController {
    
    private final ACService acService;


    @GetMapping("/doSigefes")
    public ResponseEntity<?> getOrgaosSigefes () {

        List<Orgao> orgaos = acService.getOrgaos();

        return ResponseEntity.ok(orgaos.stream().map(OrgaoDto::parse).toList());

    }


}
