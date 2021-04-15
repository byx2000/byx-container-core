package byx.ioc.exception;

public class LoadExtensionException extends ByxContainerException {
    public LoadExtensionException(Throwable cause) {
        super("Error occurred when load extensions.", cause);
    }
}
