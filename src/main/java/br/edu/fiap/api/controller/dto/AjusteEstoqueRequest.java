package br.edu.fiap.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AjusteEstoqueRequest(
        @Schema(example = "10")
        @NotNull int quantidade,
        @Schema(example = "Ajuste de estoque devido a contagem física")
        @Size(max = 200) String motivo
) {
}


