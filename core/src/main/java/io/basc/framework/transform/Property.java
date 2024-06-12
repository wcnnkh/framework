package io.basc.framework.transform;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.Parameter;

/**
 * 属性定义
 * 
 * @author wcnnkh
 *
 */
public interface Property extends Parameter {

	/**
	 * 是否只读
	 * 
	 * @return
	 */
	default boolean isReadOnly() {
		return false;
	}

	/**
	 * 插入值时需要的类型, 默认情况下和{@link #getTypeDescriptor()}相同
	 * 
	 * @see #setValue(Object)
	 * @return
	 */
	default TypeDescriptor getRequiredTypeDescriptor() {
		return getTypeDescriptor();
	}

	/**
	 * 设置值
	 * 
	 * @param value
	 * @throws UnsupportedOperationException 只读属性,不能操作
	 */
	void setValue(Object value) throws UnsupportedOperationException;

	/**
	 * 属性是否存在
	 */
	@Override
	default boolean isPresent() {
		return Parameter.super.isPresent();
	}
}
