package scw.async;

import java.io.Serializable;
import java.util.concurrent.Callable;

public interface AsyncRunnable extends Serializable, Callable<Object> {
	Object call() throws Exception;
}
