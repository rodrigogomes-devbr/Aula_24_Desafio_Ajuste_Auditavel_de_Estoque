package br.edu.fiap.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.edu.fiap.api.entity.Estoque;
import br.edu.fiap.api.entity.MovimentacaoEstoque;
import br.edu.fiap.api.entity.Produto;
import br.edu.fiap.api.exception.AjusteSemAlteracaoException;
import br.edu.fiap.api.exception.QuantidadeEstoqueInvalidaException;
import br.edu.fiap.api.repository.EstoqueRepository;
import br.edu.fiap.api.repository.MovimentacaoEstoqueRepository;
import br.edu.fiap.api.repository.ProdutoRepository;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Testes unitários do ajuste auditável de estoque.
 *
 * <p>Comprova sucesso, validação e ausência de gravação quando a regra
 * de negócio é violada.</p>
 */
class EstoqueServiceTest {
    private EstoqueRepositorioFalso estoqueFalso;
    private MovimentacaoRepositorioFalso movimentacaoFalso;
    private EstoqueService service;

    @BeforeEach
    void preparar() {
        estoqueFalso = new EstoqueRepositorioFalso();
        movimentacaoFalso = new MovimentacaoRepositorioFalso();

        EstoqueRepository estoqueRepository = (EstoqueRepository) Proxy.newProxyInstance(
                EstoqueRepository.class.getClassLoader(),
                new Class<?>[] {EstoqueRepository.class},
                estoqueFalso);

        MovimentacaoEstoqueRepository movimentacaoRepository =
                (MovimentacaoEstoqueRepository) Proxy.newProxyInstance(
                        MovimentacaoEstoqueRepository.class.getClassLoader(),
                        new Class<?>[] {MovimentacaoEstoqueRepository.class},
                        movimentacaoFalso);

        ProdutoRepository produtoRepository = (ProdutoRepository) Proxy.newProxyInstance(
                ProdutoRepository.class.getClassLoader(),
                new Class<?>[] {ProdutoRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "findById" -> Optional.empty();
                    case "toString" -> "ProdutoRepositorioFalso";
                    default -> throw new UnsupportedOperationException(method.getName());
                });

        service = new EstoqueService(estoqueRepository, produtoRepository, movimentacaoRepository);
    }

    @Test
    void deveAjustarSaldoERegistrarMovimentacao() {
        Estoque estoque = estoque(18);
        estoqueFalso.encontrado = Optional.of(estoque);

        MovimentacaoEstoque movimentacao =
                service.ajustar(1L, 16, "Contagem fisica do inventario");

        assertThat(estoque.getQuantidade()).isEqualTo(16);
        assertThat(movimentacao.getQuantidadeAnterior()).isEqualTo(18);
        assertThat(movimentacao.getQuantidadeNova()).isEqualTo(16);
        assertThat(movimentacao.getMotivo()).isEqualTo("Contagem fisica do inventario");
        assertThat(movimentacaoFalso.salvo).isNotNull();
    }

    @Test
    void deveRecusarAjusteParaOMesmoSaldo() {
        Estoque estoque = estoque(20);
        estoqueFalso.encontrado = Optional.of(estoque);

        assertThatThrownBy(() -> service.ajustar(1L, 20, "Sem alteracao"))
                .isInstanceOf(AjusteSemAlteracaoException.class);

        assertThat(estoque.getQuantidade()).isEqualTo(20);
        assertThat(movimentacaoFalso.salvo).isNull();
    }

    @Test
    void deveRecusarSaldoNegativoSemGravarHistorico() {
        Estoque estoque = estoque(18);
        estoqueFalso.encontrado = Optional.of(estoque);

        assertThatThrownBy(() -> service.ajustar(1L, -1, "Valor invalido"))
                .isInstanceOf(QuantidadeEstoqueInvalidaException.class);

        assertThat(estoque.getQuantidade()).isEqualTo(18);
        assertThat(estoqueFalso.salvo).isNull();
        assertThat(movimentacaoFalso.salvo).isNull();
    }

    private Estoque estoque(int quantidade) {
        Produto produto = new Produto("Teclado", new BigDecimal("299.90"), true, null);
        return new Estoque(produto, quantidade);
    }

    /**
     * Dublê do repository de estoque, no mesmo padrão do ProdutoServiceTest.
     */
    private static final class EstoqueRepositorioFalso implements InvocationHandler {
        private Optional<Estoque> encontrado = Optional.empty();
        private Estoque salvo;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "findById" -> encontrado;
                case "save" -> {
                    salvo = (Estoque) args[0];
                    yield salvo;
                }
                case "toString" -> "EstoqueRepositorioFalso";
                default -> throw new UnsupportedOperationException(
                        "Operacao nao implementada no teste: " + method.getName());
            };
        }
    }

    /**
     * Dublê do repository de movimentações.
     */
    private static final class MovimentacaoRepositorioFalso implements InvocationHandler {
        private MovimentacaoEstoque salvo;
        private List<MovimentacaoEstoque> historico = List.of();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            return switch (method.getName()) {
                case "save" -> {
                    salvo = (MovimentacaoEstoque) args[0];
                    yield salvo;
                }
                case "findByEstoque_IdOrderByCriadoEmDesc" -> historico;
                case "toString" -> "MovimentacaoRepositorioFalso";
                default -> throw new UnsupportedOperationException(
                        "Operacao nao implementada no teste: " + method.getName());
            };
        }
    }
}