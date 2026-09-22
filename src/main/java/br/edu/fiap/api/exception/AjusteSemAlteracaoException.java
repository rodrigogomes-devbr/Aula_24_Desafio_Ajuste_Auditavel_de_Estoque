package br.edu.fiap.api.exception;

public class AjusteSemAlteracaoException extends RuntimeException {
    public AjusteSemAlteracaoException(Long id, int quantidade) {
        super("Ajuste sem alteração para o estoque com ID " + id + " e quantidade " + quantidade);
    }
}
