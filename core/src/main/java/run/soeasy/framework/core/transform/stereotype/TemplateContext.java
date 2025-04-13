package run.soeasy.framework.core.transform.stereotype;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.core.attribute.SimpleAttributes;
import run.soeasy.framework.core.convert.Source;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TemplateContext<K, V extends Source, T extends Template<K, ? extends V>>
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
