package scw.core;

public interface Iterator<T> {
	/**
	 * @param data
	 * @return 如果返回false就终止迭代
	 */
	boolean iterator(T data);
}
