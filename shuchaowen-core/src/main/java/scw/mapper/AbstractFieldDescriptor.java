package scw.mapper;

import scw.lang.NotSupportedException;

public abstract class AbstractFieldDescriptor
		implements FieldDescriptor {
	private static final long serialVersionUID = 1L;
	private final Class<?> declaringClass;

	public AbstractFieldDescriptor(Class<?> declaringClass) {
		this.declaringClass = declaringClass;
	}

	protected NotSupportedException createNotSupportException() {
		return new NotSupportedException("class=["
				+ getDeclaringClass() + "] column [" + getName() + "]");
	}
	
	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof FieldDescriptor) {
			return ((FieldDescriptor) obj).getType() == getType()
					&& ((FieldDescriptor) obj).getName().equals(
							getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getName().hashCode() + getType().hashCode();
	}
	
	@Override
	public String toString() {
		return "declaringClass [" + getDeclaringClass() + "] name [" + getName() + "]";
	}
}
