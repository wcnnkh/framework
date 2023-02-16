package io.basc.framework.util;

public interface ParentDiscover<T extends ParentDiscover<T>> {
	T getParent();

	default boolean hasParent() {
		return getParent() != null;
	}
	
	default ReversibleIterator<T> parents(){
		return new Parents<>(this);
	}
	
	default boolean isParents(T parent) {
		if (parent == null || !hasParent()) {
			return false;
		}
		
		T p = getParent();
		while(true) {
			if(p == parent || parent.equals(p)) {
				return true;
			}
			
			if(!p.hasParent()) {
				return false;
			}
			
			p = p.getParent();
		}
	}
}
