package run.soeasy.framework.core.convert.service;

import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.comparator.TypeComparator;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.ClassUtils;

public class ConvertiblePair implements Comparable<ConvertiblePair>, Convertible {
	private final Class<?> sourceType;

	private final Class<?> targetType;

	/**
	 * Create a new source-to-target pair.
	 * 
	 * @param sourceType the source type
	 * @param targetType the target type
	 */
	public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
		Assert.notNull(sourceType, "Source type must not be null");
		Assert.notNull(targetType, "Target type must not be null");
		this.sourceType = sourceType;
		this.targetType = targetType;
	}

	public Class<?> getSourceType() {
		return this.sourceType;
	}

	public Class<?> getTargetType() {
		return this.targetType;
	}

	public ConvertiblePair reversed() {
		return new ConvertiblePair(targetType, sourceType);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || other.getClass() != ConvertiblePair.class) {
			return false;
		}
		ConvertiblePair otherPair = (ConvertiblePair) other;
		return (this.sourceType == otherPair.sourceType && this.targetType == otherPair.targetType);
	}

	@Override
	public int hashCode() {
		return (this.sourceType.hashCode() * 31 + this.targetType.hashCode());
	}

	@Override
	public String toString() {
		return (this.sourceType.getName() + " -> " + this.targetType.getName());
	}

	public int compareTo(ConvertiblePair o) {
		// 目的是为了让小类型排在前
		int v = TypeComparator.DEFAULT.compare(sourceType, o.sourceType);
		int ov = TypeComparator.DEFAULT.compare(targetType, o.targetType);
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
	public boolean canConvert(@NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType) {
		return ClassUtils.isAssignable(this.sourceType, sourceType.getType())
				&& ClassUtils.isAssignable(this.targetType, targetType.getType());
	}
}
