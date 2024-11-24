package io.basc.framework.rpc.http;

import java.lang.reflect.Method;
import java.net.URI;

import io.basc.framework.core.env.Environment;
import io.basc.framework.lang.Nullable;

public interface HttpRemoteResolver {
	boolean canResolve(Class<?> clazz);

	URI resolve(Class<?> clazz, @Nullable Environment environment);

	boolean canResolve(Method method);

	URI resolve(Method method, @Nullable Environment environment);

	default boolean canResolve(Class<?> clazz, Method method) {
		return canResolve(clazz) || canResolve(method);
	}

	default URI resolve(Class<?> clazz, Method method, @Nullable Environment environment) {
		if (canResolve(method)) {
			return resolve(method, environment);
		}

		if (canResolve(clazz)) {
			return resolve(clazz, environment);
		}
		return null;
	}
}
