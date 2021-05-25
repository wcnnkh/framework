package scw.beans.support;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.ConfigurableBeanFactory;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public abstract class AbstractBeanDefinitionLoaderChain implements
		BeanDefinitionLoaderChain {
	private static Logger logger = LoggerFactory
			.getLogger(AbstractBeanDefinitionLoaderChain.class);

	private BeanDefinitionLoaderChain chain;

	public AbstractBeanDefinitionLoaderChain(BeanDefinitionLoaderChain chain) {
		this.chain = chain;
	}

	public final BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
		BeanDefinitionLoader loader = getNext(beanFactory, sourceClass);
		if (loader == null) {
			return chain == null ? null : chain
					.load(beanFactory, sourceClass);
		}

		try {
			return loader.load(beanFactory, sourceClass, this);
		} catch (NoClassDefFoundError e) {
			if(logger.isDebugEnabled()){
				logger.debug("ignore bean builder loader [{}] ERROR [{}] Reason [{}]",
						loader.getClass(), e.getClass(), e.getMessage());
			}
			return load(beanFactory, sourceClass);
		}
	}

	protected abstract BeanDefinitionLoader getNext(ConfigurableBeanFactory beanFactory, Class<?> sourceClass);
}
