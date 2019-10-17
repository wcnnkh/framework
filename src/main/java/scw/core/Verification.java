package scw.core;

import scw.core.annotation.Ignore;

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
