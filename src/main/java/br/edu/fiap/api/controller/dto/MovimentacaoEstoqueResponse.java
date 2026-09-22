package br.edu.fiap.api.controller.dto;

import br.edu.fiap.api.entity.Estoque;
import br.edu.fiap.api.entity.MovimentacaoEstoque;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public record MovimentacaoEstoqueResponse(
        @Schema(example = "1") Long estoqueId,
        @Schema(example = "9") int quantidadeAnterior,
        @Schema(example = "10") int quantidadeNova,
        @Schema(example = "Ajuste de estoque e contagem") String motivo,
        @Schema(example = "2024-06-10T15:30:00") Instant criadoEm
) {

    public static MovimentacaoEstoqueResponse de(MovimentacaoEstoque movimentacao){
        return new MovimentacaoEstoqueResponse(
                movimentacao.getEstoque().getId(),
                movimentacao.getQuantidadeAnterior(),
                movimentacao.getQuantidadeNova(),
                movimentacao.getMotivo(),
                movimentacao.getCriadoEm()

        );
    }
}



