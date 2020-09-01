package scw.mapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractFields implements Fields {
	public Field getFirst() {
		for (Field field : this) {
			return field;
		}
		return null;
	}

	public Field find(FieldFilter... filters) {
		FieldFilters fieldFilters = new FieldFilters(filters);
		for (Field field : this) {
			if (fieldFilters.accept(field)) {
				return field;
			}
		}
		return null;
	}

	public List<Field> toList(FieldFilter... filters) {
		List<Field> list = new ArrayList<Field>();
		FieldFilters fieldFilters = new FieldFilters(filters);
		for (Field field : this) {
			if (fieldFilters.accept(field)) {
				list.add(field);
			}
		}
		return list;
	}

	public Set<Field> toSet(FieldFilter... filters) {
		Set<Field> list = new LinkedHashSet<Field>();
		FieldFilters fieldFilters = new FieldFilters(filters);
		for (Field field : this) {
			if (fieldFilters.accept(field)) {
				list.add(field);
			}
		}
		return list;
	}

	private boolean acceptFieldDescriptor(FieldDescriptor descriptor, String name, Type type) {
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

	public Field findGetter(final String name, final Type type) {
		return find(new FieldFilter() {

			public boolean accept(Field field) {
				return field.isSupportGetter() && acceptFieldDescriptor(field.getGetter(), name, type);
			}
		});
	}

	public Field findSetter(final String name, final Type type) {
		return find(new FieldFilter() {

			public boolean accept(Field field) {
				return field.isSupportSetter() && acceptFieldDescriptor(field.getSetter(), name, type);
			}
		});
	}

	public Field find(final String name, final Type type) {
		return find(new FieldFilter() {

			public boolean accept(Field field) {
				return (field.isSupportGetter() && acceptFieldDescriptor(field.getGetter(), name, type))
						|| (field.isSupportSetter() && acceptFieldDescriptor(field.getSetter(), name, type));
			}
		});
	}
}
