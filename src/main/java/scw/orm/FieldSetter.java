package scw.orm;

public final class FieldSetter implements Setter {
	private final Object object;

	public FieldSetter(Object object) {
		this.object = object;
	}

	public void setter(MappingContext context, Object value) throws ORMException {
		context.getColumn().set(object, value);
	}

	public Object getObject() {
		return object;
	}

}
