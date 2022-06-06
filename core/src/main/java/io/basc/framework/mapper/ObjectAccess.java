package io.basc.framework.mapper;

import java.util.Enumeration;

import io.basc.framework.value.Value;

public interface ObjectAccess<E extends Throwable> {
	Enumeration<String> keys() throws E;

	Value get(String name) throws E;

	void set(String name, Value value) throws E;
}
