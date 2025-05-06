package run.soeasy.framework.core.convert.mapping.collection;

import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.AccessibleDescriptor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValue;
import run.soeasy.framework.core.convert.TypedValueAccessor;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("rawtypes")
public class GenericMapValueAccessor implements TypedValueAccessor {
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
		return conversionService.apply(TypedValue.of(value),
				AccessibleDescriptor.forTypeDescriptor(mapTypeDescriptor.getMapValueTypeDescriptor()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(Object value) throws UnsupportedOperationException {
		Object target = conversionService.apply(TypedValue.of(value),
				AccessibleDescriptor.forTypeDescriptor(mapTypeDescriptor.getMapValueTypeDescriptor()));
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
