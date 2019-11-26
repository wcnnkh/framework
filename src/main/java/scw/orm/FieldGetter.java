package scw.orm;

public final class FieldGetter implements Getter {
	private final Object object;

	public FieldGetter(Object object) {
		this.object = object;
	}

	public Object getter(MappingContext context) throws Exception {
		return context.getFieldDefinition().get(object);
	}

	public Object getObject() {
		return object;
	}
}
