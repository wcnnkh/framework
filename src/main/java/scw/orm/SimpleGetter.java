package scw.orm;

public class SimpleGetter implements Getter {
	private Object value;

	public SimpleGetter(Object value) {
		this.value = value;
	}

	public Object getter(MappingContext context) throws Exception {
		return value;
	}
}
