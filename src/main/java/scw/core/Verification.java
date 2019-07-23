package scw.core;

/**
 * 校验
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Verification<T> {
	boolean verification(T data);
}
