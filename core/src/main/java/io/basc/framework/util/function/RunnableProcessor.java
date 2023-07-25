package io.basc.framework.util.function;

/**
 * @see Runnable
 * @author wcnnkh
 *
 * @param <E>
 */
@FunctionalInterface
public interface RunnableProcessor<E extends Throwable> {
	void process() throws E;
}