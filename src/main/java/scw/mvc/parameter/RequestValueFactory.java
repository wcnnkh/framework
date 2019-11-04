package scw.mvc.parameter;

import java.lang.reflect.Type;

import scw.core.ValueFactory;

public interface RequestValueFactory extends ValueFactory<String> {
	<T> T getObject(Class<T> type);

	<T> T getObject(Type type);
}
