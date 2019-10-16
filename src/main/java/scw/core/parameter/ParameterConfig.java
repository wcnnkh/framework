package scw.core.parameter;

import java.lang.reflect.Type;

public interface ParameterConfig {
	String getName();

	Class<?> getType();

	Type getGenericType();
}
