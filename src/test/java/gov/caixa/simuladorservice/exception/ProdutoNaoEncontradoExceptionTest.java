package gov.caixa.simuladorservice.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoNaoEncontradoExceptionTest {

    @Test
    void testExceptionMessage() {
        String mensagem = "Nenhum produto compatível encontrado";

        ProdutoNaoEncontradoException exception = new ProdutoNaoEncontradoException(mensagem);

        assertNotNull(exception);
        assertEquals(mensagem, exception.getMessage());
    }
}
