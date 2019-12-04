package scw.core;

import scw.lang.Ignore;

/**
 * 校验
 * 
 * @author shuchaowen
 *
 * @param <T>
 */

@Ignore
public interface Verification<T> {
	boolean verification(T data);
}
