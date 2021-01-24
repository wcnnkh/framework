package scw.configure.convert;

import scw.configure.support.EntityConfigure;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.instance.factory.NoArgsInstanceFactory;

public abstract class EntityConversionService extends EntityConfigure implements ConversionService{
	private final NoArgsInstanceFactory instanceFactory;
	
	public EntityConversionService(ConversionService conversionService, NoArgsInstanceFactory instanceFactory) {
		super(conversionService);
		this.instanceFactory = instanceFactory;
	}
	
	@Override
	public boolean isSupported(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return super.isSupported(sourceType, targetType) && instanceFactory.isInstance(targetType.getType());
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(source == null){
			return null;
		}
		
		Object target = instanceFactory.getInstance(targetType.getType());
		configuration(source, sourceType, target, targetType);
		return target;
	}

}
