package br.edu.fiap.api.repository;

import br.edu.fiap.api.entity.Estoque;
import br.edu.fiap.api.entity.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {
}
