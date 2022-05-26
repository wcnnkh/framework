package io.basc.framework.orm.support;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ReversibleMapperFactory;
import io.basc.framework.mapper.ReversibleMapperFactoryWrapper;
import io.basc.framework.mapper.SimpleReverseMapperFactory;
import io.basc.framework.orm.ObjectMapper;
import io.basc.framework.orm.repository.DefaultRepositoryMapper;
import io.basc.framework.util.Assert;

public abstract class AbstractObjectMapper<S, E extends Throwable> extends DefaultRepositoryMapper
		implements ObjectMapper<S, E>, ConversionServiceAware, ReversibleMapperFactoryWrapper<S, E> {
	private ReversibleMapperFactory<S, E> sourceConverterFactory = new SimpleReverseMapperFactory<>();
	private ConversionService conversionService;

	public ConversionService getConversionService() {
		return conversionService == null ? Sys.env.getConversionService() : conversionService;
	}

	@Override
	public void setConversionService(@Nullable ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public ReversibleMapperFactory<S, E> getSourceConverterFactory() {
		return sourceConverterFactory;
	}

	public void setSourceConverterFactory(ReversibleMapperFactory<S, E> sourceConverterFactory) {
		Assert.requiredArgument(sourceConverterFactory != null, "sourceConverterFactory");
		this.sourceConverterFactory = sourceConverterFactory;
	}
}
