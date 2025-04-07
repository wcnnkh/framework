package run.soeasy.framework.core.transform.stereotype;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.collection.Elements;

@Getter
@Setter
public class SimpleTemplateReader<K, SV extends Source, S extends Template<K, ? extends SV>, TV extends Accessor, T extends Template<K, ? extends TV>, E extends Throwable>
		implements TemplateReader<K, SV, S, TV, T, E> {

	@Override
	public Elements<? extends SV> readFrom(TemplateContext<K, SV, S> sourceContext, @NonNull S source,
			@NonNull TypeDescriptor sourceType, TemplateContext<K, TV, T> targetContext, @NonNull T target,
			@NonNull TypeDescriptor targetType, @NonNull K index, @NonNull TV targetAccessor) throws E {
		return source.getAccessors(index);
	}

}
