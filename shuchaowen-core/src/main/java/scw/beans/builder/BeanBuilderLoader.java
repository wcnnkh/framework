package scw.beans.builder;

public interface BeanBuilderLoader {
	BeanBuilder loading(LoaderContext context, BeanBuilderLoaderChain loaderChain);
}