package io.basc.framework.mapper;

import java.util.Enumeration;

public interface ObjectAccess<E extends Throwable> {
	Enumeration<String> keys() throws E;

	Parameter get(String name) throws E;

	void set(Parameter parameter) throws E;
}
