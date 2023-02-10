package io.basc.framework.util;

public interface ParentDiscover<T extends ParentDiscover<T>> {
	T getParent();

	default boolean hasParent() {
		return getParent() != null;
	}
	
	default ReversibleIterator<T> parents(){
		return new Parents<>(this);
	}
}
