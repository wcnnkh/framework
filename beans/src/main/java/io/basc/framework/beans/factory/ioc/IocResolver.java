package io.basc.framework.beans.factory.ioc;

import java.lang.reflect.Method;

public interface IocResolver {
	
	
	boolean isInitMethod(Method method);

	boolean isDestroyMethod(Method method);
}
