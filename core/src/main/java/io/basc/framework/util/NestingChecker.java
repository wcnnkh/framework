package io.basc.framework.util;

/**
 * 嵌套检查器
 * 
 * <pre>
 * if(isNestingExists(element)){ return ; }
 * 
 * Registration registration = registerNestedElement(element); try{ xxxxxx
 * }finally{ registration.unregister(); }
 * 
 * <pre>
 * 
 * @author wcnnkh
 *
 * @param <E>
 */
public interface NestingChecker<E> {
	@SuppressWarnings("unchecked")
	public static <T> NestingChecker<T> empty() {
		return (NestingChecker<T>) EmptyNestingChecker.EMPTY;
	}

	/**
	 * 判断是否存在嵌套
	 * 
	 * @param element
	 * @return
	 */
	boolean isNestingExists(E element);

	/**
	 * 注册一个嵌套元素
	 * 
	 * @param element
	 * @return
	 */
	Registration registerNestedElement(E element);
}
