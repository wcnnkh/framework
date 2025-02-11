package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.Value;
import io.basc.framework.util.attribute.SimpleAttributes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TemplateContext<K, V extends Value, T extends Template<K, ? extends V>>
		extends SimpleAttributes<String, Object> {
	private final T template;
	private final K index;
	private final V accessor;
	private final TemplateContext<K, V, T> parent;

	public TemplateContext(@NonNull T template, @NonNull K index, @NonNull V accessor) {
		this(template, index, accessor, null);
	}

	public TemplateContext(@NonNull T template, @NonNull K index, @NonNull V accessor,
			TemplateContext<K, V, T> parent) {
		this.template = template;
		this.index = index;
		this.accessor = accessor;
		this.parent = parent;
	}
}
