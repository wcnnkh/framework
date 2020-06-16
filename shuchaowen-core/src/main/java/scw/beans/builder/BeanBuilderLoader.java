package scw.beans.builder;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface BeanBuilderLoader {
	BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain loaderChain);
}