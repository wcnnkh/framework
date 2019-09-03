package scw.mvc;

import java.lang.reflect.Type;

import scw.core.ValueFactory;

public interface ParameterChannel extends Channel, ValueFactory<String> {

	Object getObject(Type type);

	Object getObject(Class<?> type);
}
