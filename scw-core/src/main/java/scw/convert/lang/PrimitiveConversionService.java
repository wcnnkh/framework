package scw.convert.lang;

import scw.convert.ConversionException;
import scw.convert.ConversionService;
import scw.convert.ConversionServiceAware;
import scw.convert.TypeDescriptor;
import scw.value.Value;

public class PrimitiveConversionService implements ConversionService, ConversionServiceAware{
	private final Value value;
	private ConversionService conversionService;
	
	public PrimitiveConversionService(Value value) {
		this.value = value;
	}
	
	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	
	@Override
	public boolean canConvert(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return sourceType == null && targetType.isPrimitive();
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) throws ConversionException {
		if(source == null){
			return value.getAsObject(targetType.getResolvableType());
		}else{
			//不做非空验证,出现此问题可以抛出空指针异常，后面再改为ConversionException
			return conversionService.convert(source, TypeDescriptor.forObject(source), targetType);
		}
	}

}
