package scw.util;

/**
 * 一个处理者的定义
 * 
 * @author shuchaowen
 *
 * @param <S> 数据来源
 * @param <T> 返回的结果
 * @param <E> 异常
 */
@FunctionalInterface
public interface Processor<S, T, E extends Throwable> {
	T process(S source) throws E;
}
