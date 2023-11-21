package io.basc.framework.context.support;

import java.util.Map.Entry;
import java.util.TreeMap;

import io.basc.framework.context.ApplicationContext;
import io.basc.framework.util.ClassUtils;

/**
 * @see GenericApplicationContext#start()
 * @author wcnnkh
 *
 */
public class ContextLoader {
	private static TreeMap<ClassLoader, ApplicationContext> applicationMap = new TreeMap<>((c1, c2) -> {
		// TODO
		return 0;
	});

	protected static void setApplicationContext(ApplicationContext applicationContext) {
		synchronized (applicationMap) {
			applicationMap.put(applicationContext.getClassLoader(), applicationContext);
		}
	}

	protected static void removeApplicationContext(ApplicationContext applicationContext) {
		for(Entry<ClassLoader, ApplicationContext> entry : applicationMap.entrySet()) {
			
		}
	}

	public static <T extends ApplicationContext> T getApplicationContext(ClassLoader classLoader,
			Class<T> requiredType) {
		return null;
	}

	public static ApplicationContext getApplicationContext(ClassLoader classLoader) {
		return getApplicationContext(classLoader, ApplicationContext.class);
	}

	public static ApplicationContext getCurrentApplicationContext() {
		return getApplicationContext(ClassUtils.getDefaultClassLoader());
	}
}
