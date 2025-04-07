package run.soeasy.framework.core.transform.stereotype.collection;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.transform.stereotype.Properties;
import run.soeasy.framework.core.transform.stereotype.Property;
import run.soeasy.framework.util.collections.Elements;

@SuppressWarnings({ "rawtypes", "unchecked" })
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MapProperties implements Properties {
	@NonNull
	private final Map map;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private ConversionService conversionService = SystemConversionService.getInstance();

	@Override
	public Elements<Property> getElements() {
		return Elements.of(map.keySet()).map((key) -> createProperty(key));
	}

	@Override
	public Property get(String key) {
		return map.containsKey(key) ? createProperty(key) : null;
	}

	@Override
	public boolean hasKey(String key) {
		return map.containsKey(key);
	}

	@Override
	public Elements<Property> getValues(String key) {
		Property property = get(key);
		return property == null ? Elements.empty() : Elements.singleton(property);
	}

	private Property createProperty(Object key) {
		return new MapProperty(map, key, typeDescriptor.getMapValueTypeDescriptor(), conversionService);
	}

	@Override
	public Elements<Property> getAccessors(@NonNull Object key) {
		return Elements.singleton(createProperty(key));
	}

	public void put(Object key, Object value) {
		if (map == null) {
			throw new UnsupportedOperationException("The map container does not exist");
		}

		((Map<Object, Object>) map).put(key, value);
	}
}
