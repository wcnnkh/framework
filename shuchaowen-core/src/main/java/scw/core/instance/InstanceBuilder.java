package scw.core.instance;

import java.lang.reflect.Constructor;

public interface InstanceBuilder {
	Constructor<?> getConstructor();

	Object[] getArgs() throws Exception;
}
