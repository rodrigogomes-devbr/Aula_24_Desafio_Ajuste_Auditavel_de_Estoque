package br.edu.fiap.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "estoques")
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    private Produto produto;

    private int quantidade;

    protected Estoque(){}

    public Estoque(Produto produto, int quantidade){
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public void repor(int quantidade){
        if(quantidade <= 0){
            throw new IllegalArgumentException("A quantidade de reposição deve ser maior que zero");
        }

        this.quantidade += quantidade;
    }

    public void ajustar(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade de ajuste não pode ser negativa");
        }
        this.quantidade = quantidade;
    }

    public void consumir(int quantidade){
        if(quantidade <= 0){
            throw new IllegalArgumentException("A quantidade de consumo deve ser maior que zero");
        }
            //retirando      //quantidade existente
        if(quantidade > this.quantidade){
            throw new IllegalArgumentException("Saldo insuficiente para realizar a retirada/ consumo");
        }

        this.quantidade -= quantidade;
    }

    public Long getId(){
        return id;
    }

    public Produto getProduto(){
        return produto;
    }

    public int getQuantidade(){
        return quantidade;
    }
}
