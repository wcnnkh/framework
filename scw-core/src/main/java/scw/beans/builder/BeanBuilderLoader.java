package scw.beans.builder;

import scw.beans.BeanDefinition;

public interface BeanBuilderLoader {
	BeanDefinition loading(LoaderContext context,
			BeanBuilderLoaderChain loaderChain);
}