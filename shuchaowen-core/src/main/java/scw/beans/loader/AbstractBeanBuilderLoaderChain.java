package scw.beans.loader;

import scw.beans.builder.AutoBeanBuilder;
import scw.beans.builder.BeanBuilder;
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

	public final BeanBuilder loading(LoaderContext context) {
		BeanBuilderLoader loader = getNext(context);
		if (loader == null) {
			return chain == null ? new AutoBeanBuilder(context) : chain
					.loading(context);
		}

		try {
			return loader.loading(context, this);
		} catch (NoClassDefFoundError e) {
			logger.debug("ignore bean builder loader [{}] Reason: {}",
					loader.getClass(), e.getMessage());
			return loading(context);
		}
	}

	protected abstract BeanBuilderLoader getNext(LoaderContext context);
}
