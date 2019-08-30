package scw.mvc.support;

import java.lang.reflect.Type;

import scw.core.ValueFactory;
import scw.mvc.Channel;

public interface ParameterChannel extends Channel, ValueFactory<String> {

	Object getObject(Type type);

	Object getObject(Class<?> type);
}
