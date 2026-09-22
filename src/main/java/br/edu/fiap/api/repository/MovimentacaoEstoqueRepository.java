package br.edu.fiap.api.repository;

import br.edu.fiap.api.entity.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    @EntityGraph(attributePaths = "estoque")
    List<MovimentacaoEstoque> findByEstoque_IdOrderByCriadoEmDesc(Long estoqueId);
}