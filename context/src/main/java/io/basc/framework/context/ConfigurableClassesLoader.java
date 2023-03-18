package io.basc.framework.context;

import io.basc.framework.factory.ConfigurableServiceLoader;

public interface ConfigurableClassesLoader extends ClassesLoader, ConfigurableServiceLoader<Class<?>> {
}
