package run.soeasy.framework.core.transform.indexed.collection;

import java.util.Iterator;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.transform.indexed.IndexedAccessor;
import run.soeasy.framework.core.transform.indexed.PropertyMapping;

@SuppressWarnings({ "rawtypes", "unchecked" })
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MapMapping implements PropertyMapping {
	@NonNull
	private final Map map;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private ConversionService conversionService = SystemConversionService.getInstance();

	@Override
	public Iterator<IndexedAccessor> iterator() {
		return map.keySet().stream().map((key) -> createIndexedAccessor(key)).iterator();
	}

	@Override
	public IndexedAccessor get(Object key) {
		return map.containsKey(key) ? createIndexedAccessor(key) : null;
	}

	@Override
	public boolean hasKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public Elements<IndexedAccessor> getValues(Object key) {
		IndexedAccessor indexed = get(key);
		return indexed == null ? Elements.empty() : Elements.singleton(indexed);
	}

	private IndexedAccessor createIndexedAccessor(Object key) {
		return new MapIndexedAccessor(map, key, typeDescriptor.getMapValueTypeDescriptor(), conversionService);
	}

	public void put(Object key, Object value) {
		if (map == null) {
			throw new UnsupportedOperationException("The map container does not exist");
		}

		((Map<Object, Object>) map).put(key, value);
	}
}
