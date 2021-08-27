package io.basc.framework.convert;

public interface ConfigurableConversionService extends ConversionService{
	void addConversionService(ConversionService conversionService);
}
