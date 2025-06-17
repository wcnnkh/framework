package run.soeasy.framework.core.convert;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import run.soeasy.framework.core.comparator.TypeComparator;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.type.ClassUtils;

@RequiredArgsConstructor
@Getter
@ToString
public class TypeMapping implements KeyValue<Class<?>, Class<?>>, Comparable<TypeMapping>, Convertable {
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
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return ClassUtils.isAssignable(this.key, sourceTypeDescriptor.getType())
				&& ClassUtils.isAssignable(targetTypeDescriptor.getType(), this.value);
	}
}
