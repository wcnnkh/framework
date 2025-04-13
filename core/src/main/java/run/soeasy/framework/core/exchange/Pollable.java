package run.soeasy.framework.core.exchange;

/**
 * 可拉取的
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Pollable<T> {
	/**
	 * Receive a object, blocking indefinitely if necessary.
	 * 
	 * @return the next available {@link Object} or {@code null} if interrupted
	 */
	T poll();
}
