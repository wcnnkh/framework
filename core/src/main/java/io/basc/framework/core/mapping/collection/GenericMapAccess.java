package io.basc.framework.core.mapping.collection;

import java.util.Map;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.transform.Accessor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("rawtypes")
public class GenericMapAccess implements Accessor {
	@NonNull
	private final Map map;
	@NonNull
	private final Object key;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private final ConversionService conversionService;

	@Override
	public Object get() throws ConversionException {
		Object value = map.get(key);
		return conversionService.convert(Value.of(value), typeDescriptor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(Object value) throws UnsupportedOperationException {
		Object target = conversionService.convert(Value.of(value), typeDescriptor);
		map.put(key, target);
	}
}
