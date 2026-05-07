package team.jit.technicalinterviewdemo.manualregression.harness;

/** Thrown when the harness cannot resolve a runnable configuration. */
public class ManualRegressionConfigException extends RuntimeException {

    public ManualRegressionConfigException(String message) {
        super(message);
    }

    public ManualRegressionConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
