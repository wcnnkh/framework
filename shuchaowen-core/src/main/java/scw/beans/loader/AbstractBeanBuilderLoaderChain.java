package scw.beans.loader;

import scw.beans.builder.BeanBuilder;

public abstract class AbstractBeanBuilderLoaderChain implements
		BeanBuilderLoaderChain {
	private BeanBuilderLoaderChain chain;

	public AbstractBeanBuilderLoaderChain(BeanBuilderLoaderChain chain) {
		this.chain = chain;
	}

	public final BeanBuilder loading(LoaderContext context) throws Exception {
		BeanBuilderLoader autoBeanService = getNext(context);
		if (autoBeanService == null) {
			return chain == null ? null : chain.loading(context);
		}

		return autoBeanService.loading(context, this);
	}

	protected abstract BeanBuilderLoader getNext(LoaderContext context);
}
