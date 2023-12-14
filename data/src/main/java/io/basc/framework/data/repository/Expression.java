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
	private static final long serialVersionUID = 1L;

	public Expression(String name) {
		this(name, null);
	}

	public Expression(String name, Object value) {
		this(name, value, null);
	}

	public Expression(String name, Object value, @Nullable TypeDescriptor typeDescriptor) {
		super(name, value, typeDescriptor);
	}
}
