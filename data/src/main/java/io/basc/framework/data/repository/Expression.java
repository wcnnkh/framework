package io.basc.framework.data.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Parameter;

/**
 * 表达式
 * 
 * @author wcnnkh
 *
 */
public class Expression extends Parameter {

	public Expression(String name) {
		super(name);
	}

	public Expression(String name, Object value) {
		super(name, value);
	}

	public Expression(String name, Object value, @Nullable TypeDescriptor typeDescriptor) {
		super(name, value, typeDescriptor);
	}
}
