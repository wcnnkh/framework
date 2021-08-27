package io.basc.framework.mapper;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Accept;

import java.lang.reflect.Type;

public class AcceptFieldDescriptor implements Accept<FieldDescriptor> {
	private final String name;
	private final Type type;

	public AcceptFieldDescriptor(String name, @Nullable Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean accept(FieldDescriptor descriptor) {
		if(descriptor == null) {
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
