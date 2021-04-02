package scw.util;

@FunctionalInterface
public interface Creator<T> {
	/**
	  *  代码一个对象
	 * @return
	 */
	T create();
}
