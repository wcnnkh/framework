package io.basc.framework.mapper;

import io.basc.framework.util.Named;
import io.basc.framework.util.element.Elements;

/**
 * 一个映射元素的定义
 * 
 * @author wcnnkh
 *
 */
public interface Element extends Named {

	@Override
	String getName();

	/**
	 * 别名
	 * 
	 * @return
	 */
	default Elements<String> getAliasNames() {
		return Elements.empty();
	}

	default boolean isSupportGetter() {
		return !getGetters().isEmpty();
	}

	/**
	 * 在此字段上可使用的Getter方案, 例如可以通过get方法和直接访问属性两种方式
	 * 
	 * @return
	 */
	Elements<? extends Getter> getGetters();

	default MergedGetter getter() {
		return new MergedGetter(getName(), getGetters());
	}

	default MergedSetter setter() {
		return new MergedSetter(getName(), getSetters());
	}

	default boolean isSupportSetter() {
		return !getSetters().isEmpty();
	}

	/**
	 * 在此字段上可使用的Setter方案，例如通过set方法和直接访问属性两种方式
	 * 
	 * @return
	 */
	Elements<? extends Setter> getSetters();
}
