package scw.util;

/**
 * 一个回调的定义
 * 
 * @author shuchaowen
 *
 * @param <S> 回调的数据类型
 * @param <E> 异常类型
 */
@FunctionalInterface
public interface Callback<S, E extends Throwable> {
	void call(S source) throws E;
}
