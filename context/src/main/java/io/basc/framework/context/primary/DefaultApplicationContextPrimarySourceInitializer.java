package io.basc.framework.context.primary;

import io.basc.framework.beans.factory.config.BeanDefinitionRegistryPostProcessor;
import io.basc.framework.beans.factory.spi.SPI;
import io.basc.framework.context.config.ApplicationContextSourceProcessor;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.context.primary.component.DefaultApplicationContextComponentLoader;
import io.basc.framework.context.primary.resource.DefaultApplicationContextPrimaryResourceLoader;
import io.basc.framework.context.resource.DefaultApplicationContextResourceImporter;
import io.basc.framework.util.collection.Elements;
import io.basc.framework.util.io.Resource;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultApplicationContextPrimarySourceInitializer
		extends ConfigurableApplicationContextPrimarySourceProcessor {
	private final DefaultApplicationContextResourceImporter applicationContextResourceImporter = new DefaultApplicationContextResourceImporter();
	private final DefaultApplicationContextPrimaryResourceLoader primaryResourceLoader = new DefaultApplicationContextPrimaryResourceLoader();
	private final DefaultApplicationContextComponentLoader componentLoader = new DefaultApplicationContextComponentLoader();

	public DefaultApplicationContextPrimarySourceInitializer() {
		registerServiceLoader(SPI.global().getServiceLoader(ApplicationContextPrimarySourceProcessor.class));
		getExtender().registerServiceLoader(
				SPI.global().getServiceLoader(ApplicationContextPrimarySourceProcessExtender.class));
	}

	@Override
	public void process(ConfigurableApplicationContext context, Class<?> source,
			ApplicationContextSourceProcessor<? super Class<?>> chain) {
		// 导入资源
		Elements<Resource> primaryResources = primaryResourceLoader.load(context, source);
		applicationContextResourceImporter.process(context, primaryResources);

		// 扫描组件
		Elements<BeanDefinitionRegistryPostProcessor> beanDefinitionRegistryPostProcessors = componentLoader
				.load(context, source);
		beanDefinitionRegistryPostProcessors.forEach(context::addBeanFactoryPostProcessor);

		// 默认的初始化
		super.process(context, source, chain);
	}
}
