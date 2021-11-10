package io.basc.framework.rpc.http;

import java.lang.reflect.Method;
import java.net.URI;

public interface HttpRemoteResolver {
	boolean canResolve(Class<?> clazz);

	URI resolve(Class<?> clazz);

	boolean canResolve(Method method);

	URI resolve(Method method);

	default boolean canResolve(Class<?> clazz, Method method) {
		return canResolve(clazz) || canResolve(method);
	}

	default URI resolve(Class<?> clazz, Method method) {
		if (canResolve(method)) {
			return resolve(method);
		}

		if (canResolve(clazz)) {
			return resolve(clazz);
		}

		return null;
	}
}
