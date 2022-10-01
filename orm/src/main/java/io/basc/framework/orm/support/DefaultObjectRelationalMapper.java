package io.basc.framework.orm.support;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.mapper.ObjectAccessFactory;
import io.basc.framework.orm.ObjectRelationalMapper;

public class DefaultObjectRelationalMapper extends DefaultObjectMapper<Object, ConversionException>
		implements ObjectRelationalMapper, ConditionalConversionService {
	private Set<ConvertiblePair> convertiblePairs;

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (sourceType == null || targetType == null) {
			return false;
		}

		boolean b = !canDirectlyConvert(sourceType, targetType)
				&& ConditionalConversionService.super.canConvert(sourceType, targetType)
				&& (isEntity(targetType.getType()) || isEntity(sourceType.getType()));
		return b;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertiblePairs == null ? Collections.emptySet() : convertiblePairs;
	}

	@Override
	public <S> void registerObjectAccessFactory(Class<S> type,
			ObjectAccessFactory<? super S, ? extends ConversionException> factory) {
		if (convertiblePairs == null) {
			convertiblePairs = new LinkedHashSet<ConvertiblePair>();
		}
		convertiblePairs.add(new ConvertiblePair(type, Object.class));
		convertiblePairs.add(new ConvertiblePair(Object.class, type));
		super.registerObjectAccessFactory(type, factory);
	}
}