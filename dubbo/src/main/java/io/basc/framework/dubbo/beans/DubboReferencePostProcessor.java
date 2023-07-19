package io.basc.framework.dubbo.beans;

import org.apache.dubbo.config.ReferenceConfig;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.dubbo.DubboReferenceRegistry;

//TODO 理论上使用BeanPostProcessor更合理，但不知道为什么会导致无法初始化
@ConditionalOnParameters
public class DubboReferencePostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		if (context.isInstance(DubboReferenceRegistry.class)) {
			DubboReferenceRegistry referenceRegistry = context.getInstance(DubboReferenceRegistry.class);
			for (ReferenceConfig<?> config : referenceRegistry.getReferences()) {
				BeanDefinition definition = new DubboBeanDefinition(context, config.getInterfaceClass(), config);
				context.registerDefinition(definition);
			}
		}
	}
}
