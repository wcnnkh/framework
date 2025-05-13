package run.soeasy.framework.core.transform.indexed.collection;

import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.transform.indexed.IndexedAccessor;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("rawtypes")
public class MapIndexedAccessor implements IndexedAccessor {
	@NonNull
	private final Map map;
	@NonNull
	private final Object index;
	@NonNull
	private final TypeDescriptor mapTypeDescriptor;
	@NonNull
	private final ConversionService conversionService;

	@Override
	public Object get() throws ConversionException {
		Object value = map.get(index);
		return conversionService.convert(value, mapTypeDescriptor.getMapValueTypeDescriptor());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void set(Object value) throws UnsupportedOperationException {
		Object target = conversionService.convert(value, mapTypeDescriptor.getMapValueTypeDescriptor());
		map.put(index, target);
	}

	@Override
	public boolean isReadable() {
		return map.containsKey(index);
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

	@Override
	public Object getIndex() {
		return index;
	}

}
