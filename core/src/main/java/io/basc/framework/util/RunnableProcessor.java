package io.basc.framework.util;

/**
 * @see Runnable
 * @author wcnnkh
 *
 * @param <E>
 */
public interface RunnableProcessor<E extends Throwable> {
	void process() throws E;
}