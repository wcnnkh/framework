package io.basc.framework.context.config;

import io.basc.framework.util.collections.Elements;

public interface ApplicationContextSourceLoadExtender<S, T> {
	Elements<T> load(ConfigurableApplicationContext context, S source,
			ApplicationContextSourceLoader<? super S, T> chain);
}
