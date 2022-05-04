package io.basc.framework.data.repository;

import java.io.Serializable;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.env.BascObject;
import io.basc.framework.lang.Nullable;

public class RepositoryColumn extends BascObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String name;
	private final Object value;
	private final TypeDescriptor valueTypeDescriptor;

	public RepositoryColumn(String name, Object value) {
		this(name, value, null);
	}

	public RepositoryColumn(String name, Object value, @Nullable TypeDescriptor valueTypeDescriptor) {
		this.name = name;
		this.value = value;
		this.valueTypeDescriptor = valueTypeDescriptor;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public TypeDescriptor getValueTypeDescriptor() {
		return valueTypeDescriptor == null ? TypeDescriptor.forObject(value) : valueTypeDescriptor;
	}
}
