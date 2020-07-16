package scw.beans.builder;

import scw.beans.BeanDefinition;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public abstract class AbstractBeanBuilderLoaderChain implements
		BeanBuilderLoaderChain {
	private static Logger logger = LoggerUtils
			.getLogger(AbstractBeanBuilderLoaderChain.class);

	private BeanBuilderLoaderChain chain;

	public AbstractBeanBuilderLoaderChain(BeanBuilderLoaderChain chain) {
		this.chain = chain;
	}

	public final BeanDefinition loading(LoaderContext context) {
		BeanBuilderLoader loader = getNext(context);
		if (loader == null) {
			return chain == null ? new AutoBeanDefinition(context) : chain
					.loading(context);
		}

		try {
			return loader.loading(context, this);
		} catch (NoClassDefFoundError e) {
			if(logger.isDebugEnabled()){
				logger.debug("ignore bean builder loader [{}] ERROR [{}] Reason [{}]",
						loader.getClass(), e.getClass(), e.getMessage());
			}
			return loading(context);
		}
	}

	protected abstract BeanBuilderLoader getNext(LoaderContext context);
}
