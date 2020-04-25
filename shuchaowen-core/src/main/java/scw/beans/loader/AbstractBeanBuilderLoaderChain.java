package scw.beans.loader;

import scw.beans.builder.BeanBuilder;

public abstract class AbstractBeanBuilderLoaderChain implements
		BeanBuilderLoaderChain {
	private BeanBuilderLoaderChain chain;

	public AbstractBeanBuilderLoaderChain(BeanBuilderLoaderChain chain) {
		this.chain = chain;
	}

	public final BeanBuilder loading(LoaderContext context) throws Exception {
		BeanBuilderLoader loader = getNext(context);
		if (loader == null) {
			return chain == null ? null : chain.loading(context);
		}

		return loader.loading(context, this);
	}

	protected abstract BeanBuilderLoader getNext(LoaderContext context);
}
