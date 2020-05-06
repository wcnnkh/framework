package scw.mapper;

import scw.lang.Description;
import scw.lang.NotSupportedException;

public abstract class AbstractFieldMetadata
		implements FieldMetadata {
	private final Class<?> declaringClass;

	public AbstractFieldMetadata(Class<?> declaringClass) {
		this.declaringClass = declaringClass;
	}

	protected NotSupportedException createNotSupportException() {
		return new NotSupportedException("class=["
				+ getDeclaringClass() + "] column [" + getName() + "]");
	}
	
	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public String getDescription() {
		Description description = getAnnotatedElement().getAnnotation(
				Description.class);
		return description == null ? null : description.value();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof FieldMetadata) {
			return ((FieldMetadata) obj).getType() == getType()
					&& ((FieldMetadata) obj).getName().equals(
							getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + getType().hashCode();
	}
}
