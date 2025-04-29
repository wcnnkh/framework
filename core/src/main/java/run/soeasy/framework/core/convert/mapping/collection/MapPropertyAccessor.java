package run.soeasy.framework.core.convert.mapping.collection;

import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.mapping.PropertyAccessor;

public class MapPropertyAccessor extends GenericMapValueAccessor implements PropertyAccessor {

	@SuppressWarnings("rawtypes")
	public MapPropertyAccessor(@NonNull Map map, @NonNull Object key, @NonNull TypeDescriptor typeDescriptor,
			@NonNull ConversionService conversionService) {
		super(map, key, typeDescriptor, conversionService);
	}

	@Override
	public String getName() {
		Object key = getKey();
		if (key instanceof String) {
			return (String) key;
		}
		return (String) getConversionService().convert(getKey(), getMapTypeDescriptor().getMapKeyTypeDescriptor(),
				TypeDescriptor.valueOf(String.class));
	}
}
