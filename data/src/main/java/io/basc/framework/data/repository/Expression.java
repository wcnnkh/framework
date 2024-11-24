package io.basc.framework.data.repository;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.param.SimpleParameter;
import io.basc.framework.lang.Nullable;

/**
 * 表达式
 * 
 * @author wcnnkh
 *
 */
public class Expression extends SimpleParameter {

	public Expression(String name) {
		this(name, null);
	}

	public Expression(String name, Object value) {
		this(name, value, null);
	}

	public Expression(String name, Object value, @Nullable TypeDescriptor typeDescriptor) {
		setName(name);
		setValue(value);
		setTypeDescriptor(typeDescriptor);
	}
}
