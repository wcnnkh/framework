package run.soeasy.framework.core.type;

import java.util.function.BiPredicate;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import run.soeasy.framework.core.comparator.TypeComparator;
import run.soeasy.framework.core.domain.KeyValue;

@RequiredArgsConstructor
@Getter
@ToString
public class TypeMapping
		implements KeyValue<Class<?>, Class<?>>, Comparable<TypeMapping>, BiPredicate<Class<?>, Class<?>> {
	@NonNull
	private final Class<?> key;
	@NonNull
	private final Class<?> value;

	@Override
	public int compareTo(TypeMapping o) {
		int v = TypeComparator.DEFAULT.compare(this.getKey(), o.getKey());
		int ov = TypeComparator.DEFAULT.compare(o.getValue(), this.getValue());
		if (v == 0) {
			return ov;
		} else if (ov == 0) {
			return v;
		} else {
			if (ov >= 0) {
				return v;
			}
			return 0;
		}
	}

	@Override
	public TypeMapping reversed() {
		return new TypeMapping(value, key);
	}

	@Override
	public boolean test(Class<?> key, Class<?> value) {
		return ClassUtils.isAssignable(this.key, key) && ClassUtils.isAssignable(value, this.value);
	}
}
