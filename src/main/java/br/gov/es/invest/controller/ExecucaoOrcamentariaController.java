package br.gov.es.invest.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.analysis.function.Add;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import br.gov.es.invest.exception.mensagens.MensagemErroRest;
import br.gov.es.invest.model.ExecucaoOrcamentaria;
import br.gov.es.invest.model.FonteOrcamentaria;
import br.gov.es.invest.model.Investimento;
import br.gov.es.invest.model.VinculadaPor;
import br.gov.es.invest.service.ExecucaoOrcamentariaService;
import br.gov.es.invest.service.FonteOrcamentariaService;
import br.gov.es.invest.service.InvestimentoService;
import br.gov.es.invest.service.InvestimentosBIService;
import br.gov.es.invest.service.PlanoOrcamentarioService;
import jakarta.annotation.security.PermitAll;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/execucao")
@RequiredArgsConstructor
public class ExecucaoOrcamentariaController {
    
}