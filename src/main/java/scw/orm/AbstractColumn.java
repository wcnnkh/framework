package scw.orm;

import java.lang.reflect.Type;

public abstract class AbstractColumn implements Column {
	public Object get(Object obj) throws ORMException {
		try {
			if (getGetter() == null) {
				return getField().get(obj);
			} else {
				return getGetter().invoke(obj);
			}
		} catch (Exception e) {
			throw new ORMException("[ORM get failed] - column [" + getName()
					+ "]", e);
		}
	}

	public void set(Object obj, Object value) throws ORMException {
		try {
			if (getSetter() == null) {
				getField().set(obj, value);
			} else {
				getSetter().invoke(obj, value);
			}
		} catch (Exception e) {
			throw new ORMException("[ORM set failed] - column [" + getName()
					+ "]", e);
		}
	}

	public Type getGenericType() {
		if (getField() == null) {
			if (getGetter() == null) {
				return getSetter().getGenericParameterTypes()[0];
			}
			return getGetter().getGenericReturnType();
		}
		return getField().getGenericType();
	}

	public Class<?> getType() {
		if (getField() == null) {
			if (getGetter() == null) {
				return getSetter().getParameterTypes()[0];
			}
			return getGetter().getReturnType();
		}
		return getField().getType();
	}
}
