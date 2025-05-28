package run.soeasy.framework.core.transform.property;

import java.util.Collection;
import java.util.function.BiPredicate;

import lombok.NonNull;
import run.soeasy.framework.core.transform.MappingFilter;

public interface PropertyMappingFilter extends MappingFilter<Object, PropertyAccessor, TypedProperties> {
	public static final PropertyMappingFilter IGNORE_NULL = predicate((s, t) -> s.isReadable() && s.get() == null);

	public static PropertyMappingFilter ignorePropertyNames(@NonNull Collection<String> names) {
		return predicate((s, t) -> !(names.contains(s.getName()) || names.contains(t.getName())));
	}

	public static PropertyMappingFilter predicate(
			@NonNull BiPredicate<? super PropertyAccessor, ? super PropertyAccessor> predicate) {
		return new PropertyMappingPredicate(predicate);
	}
}
