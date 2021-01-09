package scw.beans.builder;

import scw.beans.BeanDefinition;

public interface BeanBuilderLoaderChain {
	BeanDefinition loading(LoaderContext context);
}
