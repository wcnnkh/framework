package scw.beans.builder;

import scw.beans.BeanDefinition;
import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface BeanBuilderLoader {
	BeanDefinition loading(LoaderContext context,
			BeanBuilderLoaderChain loaderChain);
}