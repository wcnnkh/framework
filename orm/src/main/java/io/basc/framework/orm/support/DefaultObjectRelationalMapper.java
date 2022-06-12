package io.basc.framework.orm.support;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.NodeList;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ConditionalConversionService;
import io.basc.framework.convert.lang.ConvertiblePair;
import io.basc.framework.dom.NodeListAccess;
import io.basc.framework.mapper.AnyMapAccess;
import io.basc.framework.mapper.ObjectAccessFactory;
import io.basc.framework.mapper.PropertyFactoryAccess;
import io.basc.framework.orm.ObjectRelationalMapper;
import io.basc.framework.value.PropertyFactory;

public class DefaultObjectRelationalMapper extends DefaultObjectMapper<ConversionException>
		implements ObjectRelationalMapper, ConditionalConversionService {
	private final Set<ConvertiblePair> convertiblePairs = new LinkedHashSet<ConvertiblePair>();

	public DefaultObjectRelationalMapper() {
		registerObjectAccessFactory(PropertyFactory.class, (s, e) -> new PropertyFactoryAccess<>(s));
		registerObjectAccessFactory(NodeList.class, (s, e) -> new NodeListAccess<>(s));
		registerObjectAccessFactory(Map.class, (s, e) -> new AnyMapAccess<>(s, e, getConversionService()));
	}

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
		return convertiblePairs;
	}

	@Override
	public <S> void registerObjectAccessFactory(Class<S> type,
			ObjectAccessFactory<? super S, ? extends ConversionException> factory) {
		convertiblePairs.add(new ConvertiblePair(type, Object.class));
		convertiblePairs.add(new ConvertiblePair(Object.class, type));
		super.registerObjectAccessFactory(type, factory);
	}
}