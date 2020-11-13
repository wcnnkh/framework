package scw.eureka;

import scw.beans.BeanDefinition;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.instance.annotation.Configuration;

@Configuration
public class EureakClientBeanBuilder implements BeanBuilderLoader{

	@Override
	public BeanDefinition loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		return loaderChain.loading(context);
	}

}
