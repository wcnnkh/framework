package io.basc.framework.util.check;

import java.util.Arrays;
import java.util.function.Predicate;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.Registration;

/**
 * 嵌套检查器
 * 
 * <pre>
 * if(isNestingExists(element)){ return ; }
 * 
 * Registration registration = registerNestedElement(element); try{ xxxxxx
 * }finally{ registration.cancel(); }
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
	 * 使用断言来创建一个嵌套检查
	 * 
	 * @param <T>
	 * @param predicate 返回true表示存在嵌套
	 * @return
	 */
	public static <T> NestingChecker<T> predicate(Predicate<? super T> predicate) {
		return new PredicateNestingChecker<>(predicate);
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

	/**
	 * 任意一个存在嵌套都认为存在嵌套
	 * 
	 * @param right
	 * @return
	 */
	default NestingChecker<E> or(NestingChecker<E> right) {
		if (right == null || right == EmptyNestingChecker.EMPTY) {
			return this;
		}

		if (this == EmptyNestingChecker.EMPTY) {
			return right;
		}

		return new NestingCheckers<>(Elements.of(Arrays.asList(this, right)));
	}

	/**
	 * 任意一个存在嵌套都认为存在嵌套
	 * 
	 * @param predicate 返回true表示存在嵌套
	 * @return
	 */
	default NestingChecker<E> or(Predicate<? super E> predicate) {
		if (predicate == null) {
			return this;
		}

		if (this == EmptyNestingChecker.EMPTY) {
			return predicate(predicate);
		}

		return or(predicate(predicate));
	}
}
