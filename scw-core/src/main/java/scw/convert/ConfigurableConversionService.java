package scw.convert;

public interface ConfigurableConversionService extends ConversionService{
	void addConversionService(ConversionService conversionService);
}
