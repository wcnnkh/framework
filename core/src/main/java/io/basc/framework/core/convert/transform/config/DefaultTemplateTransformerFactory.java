package io.basc.framework.core.convert.transform.config;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.Accessor;
import io.basc.framework.core.convert.transform.Template;
import io.basc.framework.core.convert.transform.TemplateTransformFilter;
import io.basc.framework.core.convert.transform.TemplateTransformer;
import io.basc.framework.core.convert.transform.stractegy.DefaultTemplateTransformer;
import io.basc.framework.core.convert.transform.stractegy.FilterableTemplateTransformer;
import io.basc.framework.core.convert.transform.stractegy.TemplateTransformFilters;
import io.basc.framework.util.sequences.uuid.UUIDSequences;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

public class DefaultTemplateTransformerFactory<K, SV extends Value, S extends Template<K, SV>, TV extends Accessor, T extends Template<K, TV>, E extends Throwable>
		implements TemplateTransformerFactory<K, SV, S, TV, T, E> {
	@EqualsAndHashCode(of = "id", callSuper = false)
	private static class InternalTemplateTransformer<K, SV extends Value, S extends Template<K, SV>, TV extends Accessor, T extends Template<K, TV>, E extends Throwable>
			extends FilterableTemplateTransformer<K, SV, S, TV, T, E> {
		private final String id;

		public InternalTemplateTransformer(
				@NonNull Iterable<? extends TemplateTransformFilter<K, SV, S, TV, T, E>> filters,
				TemplateTransformer<K, SV, S, TV, T, E> dottomlessTemplateTransformer, @NonNull String id) {
			super(filters, dottomlessTemplateTransformer);
			this.id = id;
		}
	}

	private final String id = UUIDSequences.getInstance().next();
	private final DefaultTemplateTransformer<K, SV, S, TV, T, E> dottomlessTemplateTransformer = new DefaultTemplateTransformer<>();
	private final TemplateTransformFilters<K, SV, S, TV, T, E> templateTransformFilters = new TemplateTransformFilters<>();

	@Override
	public TemplateTransformer<K, SV, S, TV, T, E> getTemplateTransformer(TypeDescriptor requiredTypeDescriptor) {
		return wrap(dottomlessTemplateTransformer);
	}

	protected TemplateTransformer<K, SV, S, TV, T, E> wrap(TemplateTransformer<K, SV, S, TV, T, E> mappingStrategy) {
		if (mappingStrategy instanceof InternalTemplateTransformer) {
			if (((InternalTemplateTransformer<?, ?, ?, ?, ?, ?>) mappingStrategy).id.equals(this.id)) {
				return mappingStrategy;
			}
		}
		return new InternalTemplateTransformer<>(templateTransformFilters, mappingStrategy, id);
	}
}
