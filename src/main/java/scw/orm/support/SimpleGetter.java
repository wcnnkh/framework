package scw.orm.support;

import scw.orm.Getter;
import scw.orm.MappingContext;
import scw.orm.ORMException;

public class SimpleGetter implements Getter {
	private Object value;

	public SimpleGetter(Object value) {
		this.value = value;
	}

	public Object getter(MappingContext context) throws ORMException {
		return value;
	}
}
