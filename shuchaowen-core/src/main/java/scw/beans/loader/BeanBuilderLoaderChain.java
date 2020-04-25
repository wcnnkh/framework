package scw.beans.loader;

import scw.beans.BeanBuilder;

public interface BeanBuilderLoaderChain {
	BeanBuilder loading(LoaderContext context) throws Exception;
}
