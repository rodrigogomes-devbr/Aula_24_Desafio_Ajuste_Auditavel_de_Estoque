package br.edu.fiap.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_estoque")
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estoque_id", nullable = false)
    private Estoque estoque;


    private int quantidadeAnterior;

    private int quantidadeNova;

    @Column(nullable = false, length = 200)
    private String motivo;

    private LocalDateTime criadoEm;

    protected MovimentacaoEstoque(){}

    public MovimentacaoEstoque(Estoque estoque, int quantidadeAnterior, int quantidadeNova, String motivo){
        this.estoque = estoque;
        this.quantidadeAnterior = quantidadeAnterior;
        this.quantidadeNova = quantidadeNova;
        this.motivo = motivo;
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public int getQuantidadeAnterior() {
        return quantidadeAnterior;
    }

    public int getQuantidadeNova() {
        return quantidadeNova;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
