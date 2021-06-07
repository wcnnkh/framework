package scw.mapper;

import java.lang.reflect.Type;

import scw.lang.Nullable;
import scw.util.Accept;

public class AcceptFieldDescriptor implements Accept<FieldDescriptor> {
	private final String name;
	private final Type type;

	public AcceptFieldDescriptor(String name, @Nullable Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean accept(FieldDescriptor descriptor) {
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
