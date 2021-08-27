package io.basc.framework.convert.lang;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.value.Value;

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
