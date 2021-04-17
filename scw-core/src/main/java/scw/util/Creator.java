package scw.util;

@FunctionalInterface
public interface Creator<T> {
	/**
	 * 创建一个对象
	 * @return
	 */
	T create();
}
