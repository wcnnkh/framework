package io.basc.framework.context.config;

import io.basc.framework.util.element.Elements;

public interface ApplicationContextSourceLoader<S, T> {
	Elements<T> load(ConfigurableApplicationContext context, S source);
}
