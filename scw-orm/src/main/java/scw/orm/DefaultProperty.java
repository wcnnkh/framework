package scw.orm;

import scw.mapper.Field;

public class DefaultProperty implements Property {
	private final Field field;
	private final ObjectRelationalMapping objectRelationalMapping;

	public DefaultProperty(Field field,
			ObjectRelationalMapping objectRelationalMapping) {
		this.field = field;
		this.objectRelationalMapping = objectRelationalMapping;
	}

	@Override
	public String getName() {
		return objectRelationalMapping.getName(field.getGetter());
	}

	@Override
	public boolean isPrimaryKey() {
		return objectRelationalMapping.isPrimaryKey(field);
	}

	@Override
	public boolean isNullable() {
		return objectRelationalMapping.isNullable(field);
	}

	@Override
	public Field getField() {
		return field;
	}
}
