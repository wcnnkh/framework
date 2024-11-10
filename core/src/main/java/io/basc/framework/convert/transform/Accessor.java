package io.basc.framework.convert.transform;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ValueWrapper;

public interface Accessor extends ValueWrapper {
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
