package io.basc.framework.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.PropertyDescriptor;

/**
 * 属性定义
 * 
 * @author wcnnkh
 *
 */
public interface Property extends PropertyDescriptor, Value {

	/**
	 * 插入值时需要的类型, 默认情况下和{@link #getTypeDescriptor()}相同
	 * 
	 * @see #setValue(Object)
	 * @return
	 */
	default TypeDescriptor getRequiredTypeDescriptor() {
		return getTypeDescriptor();
	}

	@Override
	default TypeDescriptor getTypeDescriptor() {
		return Value.super.getTypeDescriptor();
	}

	/**
	 * 属性是否存在
	 */
	@Override
	default boolean isPresent() {
		return Value.super.isPresent();
	}

	/**
	 * 是否只读
	 * 
	 * @return
	 */
	default boolean isReadOnly() {
		return false;
	}

	/**
	 * 设置值
	 * 
	 * @param value
	 * @throws UnsupportedOperationException 只读属性,不能操作
	 */
	void set(Object value) throws UnsupportedOperationException;
}
