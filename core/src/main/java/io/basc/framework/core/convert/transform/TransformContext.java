package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.Value;
import lombok.Data;
import lombok.NonNull;

@Data
public class TransformContext<K, V extends Value, T extends Template<K, ? extends V>> {
	private final T template;
	private final K index;
	private final V accessor;
	private final TransformContext<K, V, T> parent;

	public TransformContext(@NonNull T template, @NonNull K index, @NonNull V accessor) {
		this(template, index, accessor, null);
	}

	public TransformContext(@NonNull T template, @NonNull K index, @NonNull V accessor,
			TransformContext<K, V, T> parent) {
		this.template = template;
		this.index = index;
		this.accessor = accessor;
		this.parent = parent;
	}
}
