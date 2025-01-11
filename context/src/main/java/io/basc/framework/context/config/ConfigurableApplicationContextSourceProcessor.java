package io.basc.framework.context.config;

import io.basc.framework.util.spi.ConfigurableServices;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurableApplicationContextSourceProcessor<E, T extends ApplicationContextSourceProcessor<E>, R extends ApplicationContextSourceProcessExtender<E>>
		extends ConfigurableServices<T>
		implements ApplicationContextSourceProcessor<E>, ApplicationContextSourceProcessExtender<E> {
	private final ConfigurableApplicationContextSourceProcessExtender<E, R> extender = new ConfigurableApplicationContextSourceProcessExtender<>();

	@Override
	public final void process(ConfigurableApplicationContext context, E source) {
		process(context, source, null);
	}

	@Override
	public void process(ConfigurableApplicationContext context, E source,
			ApplicationContextSourceProcessor<? super E> chain) {
		for (ApplicationContextSourceProcessor<E> processor : this) {
			processor.process(context, source);
		}

		// 执行扩展点
		extender.process(context, source, chain);
	}
}
