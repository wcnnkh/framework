package scw.beans.loader;

import scw.beans.BeanBuilder;

public interface BeanBuilderLoader {
	BeanBuilder loading(LoaderContext context, BeanBuilderLoaderChain serviceChain) throws Exception;
}
