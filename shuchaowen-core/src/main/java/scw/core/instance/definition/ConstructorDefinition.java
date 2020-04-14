package scw.core.instance.definition;

import java.lang.reflect.Constructor;

public interface ConstructorDefinition {
	Constructor<?> getConstructor();

	Object[] getArgs();
}
