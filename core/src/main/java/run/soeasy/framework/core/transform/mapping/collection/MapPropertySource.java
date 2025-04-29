package run.soeasy.framework.core.transform.mapping.collection;

import java.util.Iterator;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.mapping.PropertyAccessor;
import run.soeasy.framework.core.convert.mapping.PropertyTemplate;
import run.soeasy.framework.core.convert.support.SystemConversionService;

@SuppressWarnings({ "rawtypes", "unchecked" })
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MapPropertySource implements PropertyTemplate {
	@NonNull
	private final Map map;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private ConversionService conversionService = SystemConversionService.getInstance();

	@Override
	public Iterator<PropertyAccessor> iterator() {
		return map.keySet().stream().map((key) -> createProperty(key)).iterator();
	}

	@Override
	public PropertyAccessor get(Object key) {
		return map.containsKey(key) ? createProperty(key) : null;
	}

	@Override
	public boolean hasKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public Elements<PropertyAccessor> getValues(Object key) {
		PropertyAccessor property = get(key);
		return property == null ? Elements.empty() : Elements.singleton(property);
	}

	private PropertyAccessor createProperty(Object key) {
		return new MapProperty(map, key, typeDescriptor.getMapValueTypeDescriptor(), conversionService);
	}

	public void put(Object key, Object value) {
		if (map == null) {
			throw new UnsupportedOperationException("The map container does not exist");
		}

		((Map<Object, Object>) map).put(key, value);
	}
}
