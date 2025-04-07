package run.soeasy.framework.core.transform.stereotype;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.collection.Elements;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TemplateReaderChain<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		extends SimpleTemplateReader<K, SV, S, TV, T, E> {
	@NonNull
	private final Iterator<? extends TemplateReadFilter<K, SV, S, TV, T, E>> iterator;
	private TemplateReader<K, SV, S, TV, T, E> templateReader;

	@Override
	public Elements<? extends SV> readFrom(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull TV targetAccessor) throws E {
		if (iterator.hasNext()) {
			return iterator.next().readFrom(sourceContext, source, sourceType, targetContext, target, targetType, index,
					targetAccessor, this);
		} else if (templateReader != null) {
			return templateReader.readFrom(sourceContext, source, sourceType, targetContext, target, targetType, index,
					targetAccessor);
		}
		return super.readFrom(sourceContext, source, sourceType, targetContext, target, targetType, index,
				targetAccessor);
	}
}
