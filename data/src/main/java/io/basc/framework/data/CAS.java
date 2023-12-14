package io.basc.framework.data;

import io.basc.framework.observe.Variable;
import io.basc.framework.value.Value;

public interface CAS<T> extends Value, Variable {
	@Override
	T getSource();

	/**
	 * 数据版本号
	 */
	@Override
	long lastModified();
}
