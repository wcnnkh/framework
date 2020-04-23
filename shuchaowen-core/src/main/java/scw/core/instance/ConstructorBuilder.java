package scw.core.instance;

import java.lang.reflect.Constructor;

public interface ConstructorBuilder {
	Constructor<?> getConstructor();

	Object[] getArgs() throws Exception;
}
