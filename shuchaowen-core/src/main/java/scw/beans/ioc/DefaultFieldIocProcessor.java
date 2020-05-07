package scw.beans.ioc;

import scw.mapper.FieldContext;

public abstract class DefaultFieldIocProcessor extends FieldIocProcessor {
	private final FieldContext fieldContext;

	public DefaultFieldIocProcessor(FieldContext fieldContext) {
		this.fieldContext = fieldContext;
	}

	@Override
	public FieldContext getFieldContext() {
		return fieldContext;
	}

}
