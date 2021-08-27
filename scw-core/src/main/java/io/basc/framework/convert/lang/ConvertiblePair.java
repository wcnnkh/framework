package io.basc.framework.convert.lang;

import io.basc.framework.core.Assert;
import io.basc.framework.core.utils.ClassUtils;
import io.basc.framework.lang.Nullable;

public class ConvertiblePair implements Comparable<ConvertiblePair>{

	private final Class<?> sourceType;

	private final Class<?> targetType;

	/**
	 * Create a new source-to-target pair.
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

	@Override
	public boolean equals(@Nullable Object other) {
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
		if(o.sourceType.equals(sourceType)){
			if(ClassUtils.isAssignable(targetType, targetType)){
				return 1;
			}
		}
		
		if(ClassUtils.isAssignable(sourceType, o.sourceType)){
			return 1;
		}
		return -1;
	}
}
