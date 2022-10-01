package io.basc.framework.mapper;

import java.lang.reflect.Type;
import java.util.function.Predicate;

import io.basc.framework.lang.Nullable;

public class PredicateFieldDescriptor implements Predicate<FieldDescriptor> {
	private final String name;
	private final Type type;

	public PredicateFieldDescriptor(String name, @Nullable Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean test(FieldDescriptor descriptor) {
		if (descriptor == null) {
			return false;
		}

		if (type != null) {
			if (type instanceof Class) {
				if (!type.equals(descriptor.getType())) {
					return false;
				}
			} else {
				if (!type.equals(descriptor.getGenericType())) {
					return false;
				}
			}
		}
		return name.equals(descriptor.getName());
	}
}
