package scw.beans.loader;

import scw.beans.builder.BeanBuilder;

public interface BeanBuilderLoader {
	BeanBuilder loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) throws Exception;
}
