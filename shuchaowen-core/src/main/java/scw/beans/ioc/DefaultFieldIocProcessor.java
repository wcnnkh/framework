package scw.beans.ioc;

import scw.mapper.Field;

public abstract class DefaultFieldIocProcessor extends FieldIocProcessor {
	private final Field field;

	public DefaultFieldIocProcessor(Field field) {
		this.field = field;
	}

	@Override
	public Field getField() {
		return field;
	}

}
