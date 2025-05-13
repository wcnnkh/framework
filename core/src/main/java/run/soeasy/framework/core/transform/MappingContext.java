package run.soeasy.framework.core.transform;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.attribute.SimpleAttributes;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

@Getter
@AllArgsConstructor
public class MappingContext<K, V extends TypedValueAccessor, T extends Mapping<K, V>>
		extends SimpleAttributes<String, Object> {
	private final MappingContext<K, V, T> parent;
	private final T mapping;
	private final KeyValue<K, V> keyValue;

	public MappingContext(T template) {
		this(template, null);
	}

	public MappingContext(T template, KeyValue<K, V> keyValue) {
		this(null, template, keyValue);
	}

	public boolean hasMapping() {
		return mapping != null;
	}

	public boolean hasKeyValue() {
		return keyValue != null;
	}

	public final MappingContext<K, V, T> current(KeyValue<K, V> keyValue) {
		return current(null, keyValue);
	}

	public MappingContext<K, V, T> current(T mapping, KeyValue<K, V> keyValue) {
		return new MappingContext<>(this.parent, mapping == null ? this.mapping : mapping, keyValue);
	}

	public final MappingContext<K, V, T> nested(KeyValue<K, V> keyValue) {
		return nested(null, keyValue);
	}

	public MappingContext<K, V, T> nested(T mapping, KeyValue<K, V> keyValue) {
		return new MappingContext<>(this, mapping == null ? this.mapping : mapping, keyValue);
	}
}
