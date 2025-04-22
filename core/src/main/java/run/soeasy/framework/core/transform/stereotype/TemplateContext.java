package run.soeasy.framework.core.transform.stereotype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.attribute.SimpleAttributes;
import run.soeasy.framework.core.convert.value.ValueAccessor;

@Getter
@AllArgsConstructor
public class TemplateContext<K, V extends ValueAccessor, T extends Template<K, V>> extends SimpleAttributes<String, Object> {
	private final TemplateContext<K, V, T> parent;
	private final T template;
	private final KeyValue<K, V> keyValue;

	public TemplateContext(T template) {
		this(template, null);
	}

	public TemplateContext(T template, KeyValue<K, V> keyValue) {
		this(null, template, keyValue);
	}

	public boolean hasTemplate() {
		return template != null;
	}

	public boolean hasKeyValue() {
		return keyValue != null;
	}

	public final TemplateContext<K, V, T> current(KeyValue<K, V> keyValue) {
		return current(null, keyValue);
	}

	public TemplateContext<K, V, T> current(T template, KeyValue<K, V> keyValue) {
		return new TemplateContext<>(this.parent, template == null ? this.template : template, keyValue);
	}

	public final TemplateContext<K, V, T> nested(KeyValue<K, V> keyValue) {
		return nested(null, keyValue);
	}

	public TemplateContext<K, V, T> nested(T template, KeyValue<K, V> keyValue) {
		return new TemplateContext<>(this, template == null ? this.template : template, keyValue);
	}
}
