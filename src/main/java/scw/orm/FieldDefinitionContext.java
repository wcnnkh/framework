package scw.orm;

import scw.core.reflect.FieldDefinition;

public final class FieldDefinitionContext {
	private final FieldDefinitionContext superContext;
	private final FieldDefinition fieldDefinition;

	public FieldDefinitionContext(FieldDefinitionContext superContext, FieldDefinition fieldDefinition) {
		this.superContext = superContext;
		this.fieldDefinition = fieldDefinition;
	}

	public FieldDefinitionContext getSuperContext() {
		return superContext;
	}

	public FieldDefinition getFieldDefinition() {
		return fieldDefinition;
	}
}
