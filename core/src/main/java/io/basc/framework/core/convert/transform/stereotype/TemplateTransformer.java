package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.transform.Transformer;
import io.basc.framework.util.collections.Elements;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class TemplateTransformer<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		implements Transformer<S, T, E>, TemplateWriter<K, SV, S, TV, T, E>, TemplateReader<K, SV, S, TV, T, E> {
	private final DefaultTemplateReader<K, SV, S, TV, T, E> templateReader = new DefaultTemplateReader<>();
	private final DefaultTemplateWriter<K, SV, S, TV, T, E> templateWriter = new DefaultTemplateWriter<>();

	@Override
	public Elements<? extends SV> readFrom(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull TV targetAccessor) throws E {
		return templateReader.readFrom(sourceContext, source, sourceType, targetContext, target, targetType, index,
				targetAccessor);
	}

	@Override
	public void transform(@NonNull S source, @NonNull TypeDescriptor sourceType, @NonNull T target,
			@NonNull TypeDescriptor targetType) throws E {
		transform(null, source, sourceType, null, target, targetType);
	}

	public int transform(TemplateContext<K, SV, S> sourceContext, @NonNull S source, @NonNull TypeDescriptor sourceType,
			TemplateContext<K, TV, T> targetContext, @NonNull T target, @NonNull TypeDescriptor targetType) throws E {
		int count = 0;
		for (K index : target.getAccessorIndexes()) {
			count += transform(sourceContext, source, sourceType, targetContext, target, targetType, index);
		}
		return count;
	}

	public int transform(TemplateContext<K, SV, S> sourceContext, @NonNull S source, @NonNull TypeDescriptor sourceType,
			TemplateContext<K, TV, T> targetContext, @NonNull T target, @NonNull TypeDescriptor targetType,
			@NonNull K index) throws E {
		int count = 0;
		for (TV targetAccessor : target.getAccessors(index)) {
			Elements<? extends SV> sourceElements = readFrom(sourceContext, source, sourceType, targetContext, target,
					targetType, index, targetAccessor);
			for (SV sourceElement : sourceElements) {
				if (writeTo(sourceContext, source, sourceType, targetContext, target, targetType, index, sourceElement,
						targetAccessor)) {
					count++;
					break;
				}
			}
		}
		return count;
	}

	@Override
	public boolean writeTo(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull SV sourceElement, @NonNull TV targetAccessor)
			throws E {
		return templateWriter.writeTo(sourceContext, source, sourceType, targetContext, target, targetType, index,
				sourceElement, targetAccessor);
	}
}
