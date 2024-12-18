package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Any;

public interface Access extends Any {
	@FunctionalInterface
	public static interface AccessWrapper<W extends Access> extends Access, AnyWrapper<W> {

		@Override
		default TypeDescriptor getRequiredTypeDescriptor() {
			return getSource().getRequiredTypeDescriptor();
		}

		@Override
		default boolean isReadable() {
			return getSource().isReadable();
		}

		@Override
		default boolean isWriteable() {
			return getSource().isWriteable();
		}

		@Override
		default void set(Object source) throws UnsupportedOperationException {
			getSource().set(source);
		}
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
	 * 是否可读
	 * 
	 * @return
	 */
	default boolean isReadable() {
		return true;
	}

	/**
	 * 是否可写
	 * 
	 * @return
	 */
	default boolean isWriteable() {
		return true;
	}

	/**
	 * 设置
	 * 
	 * @param source
	 * @throws UnsupportedOperationException 只读属性,不能操作
	 */
	void set(Object source) throws UnsupportedOperationException;
}
