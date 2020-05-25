package scw.beans.builder;

import scw.beans.annotation.Bean;

@Bean(proxy = false)
public interface BeanBuilderLoader {
	BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain loaderChain);
}