package br.edu.fiap.api.repository;

import br.edu.fiap.api.entity.Estoque;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    @Override
    @EntityGraph(attributePaths = "produto")
    List<Estoque> findAll();

    @Override
    @EntityGraph(attributePaths = "produto")
    Optional<Estoque> findById(Long id);

    @EntityGraph(attributePaths = "produto")
    Optional<Estoque> findByProduto_Id(Long produtoId);
}