package br.edu.fiap.api.controller;

import br.edu.fiap.api.controller.dto.*;
import br.edu.fiap.api.entity.Estoque;
import br.edu.fiap.api.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@RestController
@RequestMapping("/api/estoques")
@Tag(name = "Estoques")
public class EstoqueController {
    private final EstoqueService service;


    public EstoqueController(EstoqueService service) {
        this.service = service;
    }


    @GetMapping
    @Operation(summary = "Listar estoques")
    public List<EstoqueResponse> listar() {
        return service.listar().stream().map(EstoqueResponse::de).toList();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar estoque por ID")
    public EstoqueResponse buscar(@Parameter(example = "1") @PathVariable Long id) {
        return EstoqueResponse.de(service.buscar(id));
    }


    @PostMapping
    @Operation(summary = "Criar estoque para um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estoque criado"),
            @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
            @ApiResponse(responseCode = "409", description = "Produto já possui estoque")
    })
    public ResponseEntity<EstoqueResponse> criar(@Valid @RequestBody EstoqueRequest request) {
        Estoque salvo = service.criar(request.produtoId(), request.quantidade());
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salvo.getId())
                .toUri();
        return ResponseEntity.created(localizacao).body(EstoqueResponse.de(salvo));
    }

    @PatchMapping("/{id}/consumir")
    @Operation(summary = "Consumir unidades do estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consumo registrado"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado"),
            @ApiResponse(responseCode = "409", description = "Saldo insuficiente")
    })
    public EstoqueResponse consumir(
            @Parameter(example = "1") @PathVariable Long id,
            @Valid @RequestBody ConsumoEstoqueRequest request) {
        return EstoqueResponse.de(service.consumir(id, request.quantidade()));
    }

    @PatchMapping("/{id}/repor")
    @Operation(summary = "Repor unidades no estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reposição registrada"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado")
    })
    public EstoqueResponse repor(
            @Parameter(example = "1") @PathVariable Long id,
            @Valid @RequestBody ConsumoEstoqueRequest request) {
        return EstoqueResponse.de(service.repor(id, request.quantidade()));
    }

    @PatchMapping("/{id}/ajustar")
    @Operation(summary = "Ajustar saldo do estoque com registro de histórico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ajuste registrado"),
            @ApiResponse(responseCode = "400", description = "Quantidade ou motivo inválido"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado"),
            @ApiResponse(responseCode = "409", description = "Saldo informado é igual ao atual")
    })
    public MovimentacaoEstoqueResponse ajustar(
            @Parameter(example = "1") @PathVariable Long id,
            @Valid @RequestBody AjusteEstoqueRequest request) {
        return MovimentacaoEstoqueResponse.de(
                service.ajustar(id, request.novaQuantidade(), request.motivo()));
    }

    @GetMapping("/{id}/movimentacoes")
    @Operation(summary = "Consultar histórico de ajustes do estoque")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico recuperado"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado")
    })
    public List<MovimentacaoEstoqueResponse> listarMovimentacoes(
            @Parameter(example = "1") @PathVariable Long id) {
        return service.listarMovimentacoes(id).stream()
                .map(MovimentacaoEstoqueResponse::de)
                .toList();
    }



}
