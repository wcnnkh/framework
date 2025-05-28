package run.soeasy.framework.core.transform.property;

import java.util.function.BiPredicate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.transform.Mapper;
import run.soeasy.framework.core.transform.MappingContext;

@RequiredArgsConstructor
public class PropertyMappingPredicate implements PropertyMappingFilter {
	@NonNull
	private final BiPredicate<? super PropertyAccessor, ? super PropertyAccessor> predicate;

	@Override
	public boolean doMapping(@NonNull MappingContext<Object, PropertyAccessor, TypedProperties> sourceContext,
			@NonNull MappingContext<Object, PropertyAccessor, TypedProperties> targetContext,
			@NonNull Mapper<Object, PropertyAccessor, TypedProperties> mapper) {
		if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
			if (!predicate.test(sourceContext.getKeyValue().getValue(), targetContext.getKeyValue().getValue())) {
				return false;
			}
		}
		return mapper.doMapping(sourceContext, targetContext);
	}

}
