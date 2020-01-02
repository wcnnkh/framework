package scw.core.instance;

import java.lang.reflect.Constructor;

public interface InstanceConfig {
	Constructor<?> getConstructor();

	Object[] getArgs();
}
