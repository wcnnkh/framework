package io.basc.framework.core.convert.transform.collection;

import java.util.Map;

import io.basc.framework.core.convert.ConversionService;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Access;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("rawtypes")
public class GenericMapAccess implements Access {
	@NonNull
	private final Map map;
	@NonNull
	private final Object key;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private final ConversionService conversionService;

	@Override
	public Object getSource() {
		Object value = map.get(key);
		return conversionService.convert(value, typeDescriptor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSource(Object source) throws UnsupportedOperationException {
		Object target = conversionService.convert(source, typeDescriptor);
		map.put(key, target);
	}
}
