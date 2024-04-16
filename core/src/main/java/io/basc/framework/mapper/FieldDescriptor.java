package io.basc.framework.mapper;

import io.basc.framework.execution.Getter;
import io.basc.framework.execution.Setter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Named;
import io.basc.framework.util.element.Elements;

/**
 * 一个映射成员的定义
 * 
 * @author wcnnkh
 *
 */
public interface FieldDescriptor extends Named {

	/**
	 * 别名
	 * 
	 * @return
	 */
	default Elements<String> getAliasNames() {
		return Elements.empty();
	}

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
