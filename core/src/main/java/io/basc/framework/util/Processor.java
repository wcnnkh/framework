package io.basc.framework.util;

/**
 * @see Runnable
 * @author wcnnkh
 *
 * @param <E>
 */
@FunctionalInterface
public interface Processor<E extends Throwable> {
	@FunctionalInterface
	public static interface ProcessorWrapper<E extends Throwable, W extends Processor<E>>
			extends Processor<E>, io.basc.framework.util.Wrapper<W> {
		@Override
		default void run() throws E {
			getSource().run();
		}
	}

	void run() throws E;
}