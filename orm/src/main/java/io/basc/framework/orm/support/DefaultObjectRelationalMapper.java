package io.basc.framework.orm.support;

import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.mapper.ObjectAccessFactory;
import io.basc.framework.orm.ObjectRelationalMapper;

public class DefaultObjectRelationalMapper extends DefaultObjectMapper<ConversionException>
		implements ObjectRelationalMapper, ConditionalConversionService {
	private final Set<ConvertiblePair> convertiblePairs = new LinkedHashSet<ConvertiblePair>();
	
	public DefaultObjectRelationalMapper() {
		
	}

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return !canDirectlyConvert(sourceType, targetType)
				&& ConditionalConversionService.super.canConvert(sourceType, targetType)
				&& isEntity(targetType.getType());
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertiblePairs;
	}

	public Set<ConvertiblePair> getConvertiblePairs() {
		return convertiblePairs;
	}

	@Override
	public <S> void registerObjectAccessFactory(Class<S> type,
			ObjectAccessFactory<? super S, ? extends ConversionException> factory) {
		convertiblePairs.add(new ConvertiblePair(type, Object.class));
		super.registerObjectAccessFactory(type, factory);
	}
}