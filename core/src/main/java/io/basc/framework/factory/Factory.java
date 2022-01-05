package io.basc.framework.factory;

/**
 * 对一个创建者的定义
 * 
 * @author wcnnkh
 *
 * @param <T> 返回结果类型
 * @param <E> 异常类型
 */
@FunctionalInterface
public interface Factory<T, E extends Throwable> {
	/**
	 * 创建一个对象
	 * 
	 * @return
	 */
	T create() throws E;

	/**
	 * 单例
	 * 
	 * @see CachingFactory
	 * @return
	 */
	default Factory<T, E> single() {
		return new CachingFactory<>(this);
	}
}
