package br.edu.fiap.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

public record ConsumoEstoqueRequest(
        @Schema(description = "Quantidade que será consumida", example = "2")
        @Positive int quantidade
) {
}