package run.soeasy.framework.core.transform.collection;

import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("rawtypes")
public class MapEntryAccessor implements TypedValueAccessor {
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
		return conversionService.convert(value, mapTypeDescriptor.getMapValueTypeDescriptor());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(Object value) throws UnsupportedOperationException {
		Object target = conversionService.convert(value, mapTypeDescriptor.getMapValueTypeDescriptor());
		map.put(key, target);
	}

	@Override
	public boolean isReadable() {
		return map.containsKey(key);
	}

	@Override
	public boolean isWriteable() {
		return true;
	}

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() {
		return mapTypeDescriptor.getMapValueTypeDescriptor();
	}

	@Override
	public TypeDescriptor getReturnTypeDescriptor() {
		return mapTypeDescriptor.getMapValueTypeDescriptor();
	}
}
