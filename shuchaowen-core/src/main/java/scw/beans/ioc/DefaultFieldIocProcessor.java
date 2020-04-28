package scw.beans.ioc;

import scw.core.reflect.FieldDefinition;

public abstract class DefaultFieldIocProcessor extends FieldIocProcessor {
	private final FieldDefinition fieldDefinition;

	public DefaultFieldIocProcessor(FieldDefinition fieldDefinition) {
		this.fieldDefinition = fieldDefinition;
	}

	@Override
	public FieldDefinition getFieldDefinition() {
		return fieldDefinition;
	}

}
