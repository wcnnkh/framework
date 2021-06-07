package scw.value;

import java.util.Iterator;
import java.util.Map;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.util.CollectionFactory;
import scw.util.StringMatcher;
import scw.util.StringMatchers;
import scw.util.placeholder.PlaceholderResolver;

public interface PropertyFactory extends ValueFactory<String>, Iterable<String>, PlaceholderResolver {
	Iterator<String> iterator();

	default String resolvePlaceholder(String placeholderName) {
		return getString(placeholderName);
	}

	@SuppressWarnings("unchecked")
	default <K, V, M extends Map<K, V>> M getMap(String pattern, StringMatcher keyMatcher, TypeDescriptor mapType,
			ConversionService conversionService) {
		if (!mapType.isMap()) {
			throw new IllegalArgumentException(mapType.toString());
		}

		M map = (M) CollectionFactory.createMap(mapType.getType(), 16);
		for (String key : this) {
			if (StringMatchers.match(keyMatcher, pattern, key)) {
				Value sourceValue = getValue(key);
				if (sourceValue == null) {
					continue;
				}

				K targetKey = (K) conversionService.convert(key, TypeDescriptor.forObject(key),
						mapType.getMapKeyTypeDescriptor());
				V targetValue = (V) conversionService.convert(sourceValue, TypeDescriptor.forObject(sourceValue),
						mapType.getMapValueTypeDescriptor());
				map.put(targetKey, targetValue);
			}
		}
		return map;
	}
}
