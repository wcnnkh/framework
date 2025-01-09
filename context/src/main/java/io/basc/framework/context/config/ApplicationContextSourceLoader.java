package io.basc.framework.context.config;

import io.basc.framework.util.collection.Elements;

public interface ApplicationContextSourceLoader<S, T> {
	Elements<T> load(ConfigurableApplicationContext context, S source);
}
