package scw.util;

/**
 * 对一个创建者的定义
 * 
 * @author shuchaowen
 *
 * @param <T> 返回结果类型
 * @param <E> 异常类型
 */
@FunctionalInterface
public interface Creator<T, E extends Throwable> {
	/**
	 * 创建一个对象
	 * 
	 * @return
	 */
	T create() throws E;
}
