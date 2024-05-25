package io.basc.framework.mapper.entity;

import io.basc.framework.execution.Getter;
import io.basc.framework.execution.Setter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.property.Item;

/**
 * 一个映射成员的定义
 * 
 * @author wcnnkh
 *
 */
public interface FieldDescriptor extends Item {

	default boolean isSupportGetter() {
		return getter() != null;
	}

	@Nullable
	Getter getter();

	@Nullable
	Setter setter();

	default boolean isSupportSetter() {
		return setter() != null;
	}
}
