package scw.orm;

public final class MappingContext {
	private final MappingContext superContext;
	private final Column column;
	private final Class<?> declaringClass;

	public MappingContext(MappingContext superContext, Column column, Class<?> declaringClass) {
		this.superContext = superContext;
		this.column = column;
		this.declaringClass = declaringClass;
	}

	public MappingContext getSuperContext() {
		return superContext;
	}

	public Column getColumn() {
		return column;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}
}
