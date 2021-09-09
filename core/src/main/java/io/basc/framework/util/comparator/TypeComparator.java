package io.basc.framework.util.comparator;

import java.util.Comparator;

import io.basc.framework.util.ClassUtils;

public class TypeComparator implements Comparator<Class<?>>{

	@Override
	public int compare(Class<?> o1, Class<?> o2) {
		if(o1 == null) {
			return -1;
		}
		
		if(o2 == null) {
			return 1;
		}
		
		if(o1.equals(o2)) {
			return 0;
		}
		
		if(ClassUtils.isAssignable(o1, o2)) {
			return 1;
		}
		
		return -1;
	}

}
