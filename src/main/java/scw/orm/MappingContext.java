package scw.orm;

import scw.core.reflect.FieldDefinition;

public final class MappingContext {
	private final MappingContext superContext;
	private final FieldDefinition fieldDefinition;
	private final Class<?> declaringClass;

	public MappingContext(MappingContext superContext, FieldDefinition fieldDefinition,
			Class<?> declaringClass) {
		this.superContext = superContext;
		this.fieldDefinition = fieldDefinition;
		this.declaringClass = declaringClass;
	}

	public MappingContext getSuperContext() {
		return superContext;
	}

	public FieldDefinition getFieldDefinition() {
		return fieldDefinition;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}
}
