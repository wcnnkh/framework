package run.soeasy.framework.core.transform.collection;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.transform.templates.TemplateMapping;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(of = "map")
public class MapEntryMapping implements TemplateMapping<TypedValueAccessor> {
	@NonNull
	private final Map<?, ?> map;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private Converter converter = SystemConversionService.getInstance();
	

	@Override
	public TypedValueAccessor get(Object key) {
		return map.containsKey(key) ? createAccessor(key) : null;
	}

	@Override
	public boolean hasKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public Elements<KeyValue<Object, TypedValueAccessor>> getElements() {
		return Elements.of(() -> map.keySet().stream().map((key) -> KeyValue.of(key, createAccessor(key))));
	}

	@Override
	public Elements<TypedValueAccessor> getValues(Object key) {
		TypedValueAccessor indexed = get(key);
		return indexed == null ? Elements.empty() : Elements.singleton(indexed);
	}

	private TypedValueAccessor createAccessor(Object key) {
		return new MapEntryAccessor(map, key, typeDescriptor, converter);
	}
}
