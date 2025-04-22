package run.soeasy.framework.core.transform.mapping.collection;

import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;
import run.soeasy.framework.core.transform.stereotype.Accessor;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("rawtypes")
public class GenericMapAccess implements Accessor {
	@NonNull
	private final Map map;
	@NonNull
	private final Object key;
	@NonNull
	private final TypeDescriptor mapTypeDescriptor;
	@NonNull
	private final ConversionService conversionService;

	@Override
	public Object get() throws ConversionException {
		Object value = map.get(key);
		return conversionService.convert(ValueAccessor.of(value), mapTypeDescriptor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(Object value) throws UnsupportedOperationException {
		Object target = conversionService.convert(ValueAccessor.of(value), mapTypeDescriptor);
		map.put(key, target);
	}
}
