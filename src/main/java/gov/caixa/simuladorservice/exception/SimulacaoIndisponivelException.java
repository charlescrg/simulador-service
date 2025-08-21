package gov.caixa.simuladorservice.exception;

public class SimulacaoIndisponivelException extends RuntimeException {

    public SimulacaoIndisponivelException() {
        super();
    }

    public SimulacaoIndisponivelException(String message) {
        super(message);
    }

    public SimulacaoIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimulacaoIndisponivelException(Throwable cause) {
        super(cause);
    }
}
