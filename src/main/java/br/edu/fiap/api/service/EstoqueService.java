package br.edu.fiap.api.service;


import br.edu.fiap.api.entity.Estoque;
import br.edu.fiap.api.entity.MovimentacaoEstoque;
import br.edu.fiap.api.entity.Produto;
import br.edu.fiap.api.exception.*;
import br.edu.fiap.api.repository.EstoqueRepository;
import br.edu.fiap.api.repository.MovimentacaoEstoqueRepository;
import br.edu.fiap.api.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;

    private final MovimentacaoEstoqueRepository movimentacaoRepository;



    public EstoqueService(EstoqueRepository estoqueRepository,
                          ProdutoRepository produtoRepository,
                          MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<Estoque> listar(){
        return estoqueRepository.findAll();
    }

    public Estoque buscar(Long id){
        return estoqueRepository.findById(id)
                .orElseThrow(() -> new EstoqueNaoEncontradoException(id));
    }

    @Transactional
    public Estoque criar(Long produtoId, int quantidade){
        validarQuantidadeInicial(quantidade);
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(produtoId));
        if(estoqueRepository.findByProduto_Id(produtoId).isPresent()){
            throw new EstoqueJaCadastradoException(produtoId);
        }

        return estoqueRepository.save(new Estoque(produto, quantidade));

    }

    @Transactional
    public Estoque consumir(Long id, int quantidade){
        Estoque estoque = buscar(id);
        try{
            estoque.consumir(quantidade);
        }catch(IllegalArgumentException err){
            throw new QuantidadeEstoqueInvalidaException(err.getMessage());
        }catch(IllegalStateException err){
            throw new EstoqueInsuficienteException(id);
        }

        return estoqueRepository.save(estoque);
    }

    @Transactional
    public Estoque repor(Long id, int quantidade){
        Estoque estoque = buscar(id);
        try{
            estoque.repor(quantidade);
        }catch(IllegalArgumentException err){
            throw new QuantidadeEstoqueInvalidaException(err.getMessage());
        }

        return estoqueRepository.save(estoque);
    }

    @Transactional
    public MovimentacaoEstoque ajustar(Long id, int novaQuantidade, String motivo) {
        Estoque estoque = buscar(id);

        int quantidadeAnterior = estoque.getQuantidade();

        if (quantidadeAnterior == novaQuantidade) {
            throw new AjusteSemAlteracaoException(id, novaQuantidade);
        }

        try {
            estoque.ajustar(novaQuantidade);
        } catch (IllegalArgumentException err) {
            throw new QuantidadeEstoqueInvalidaException(err.getMessage());
        }

        estoqueRepository.save(estoque);

        return movimentacaoRepository.save(
                new MovimentacaoEstoque(estoque, quantidadeAnterior, novaQuantidade, motivo));
    }

    public List<MovimentacaoEstoque> listarMovimentacoes(Long id) {
        buscar(id);
        return movimentacaoRepository.findByEstoque_IdOrderByCriadoEmDesc(id);
    }



    public void validarQuantidadeInicial(int quantidade){
        if(quantidade < 0){
            throw new QuantidadeEstoqueInvalidaException("A quantidade inicial de estoque não pode ser negativa");
        }
    }
}
