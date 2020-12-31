package scw.configure.support;

import scw.configure.Configure;
import scw.convert.TypeDescriptor;

public abstract class AbstractConfigure implements Configure{
	//default method
	
	public void configuration(Object source, Object target) {
		if(source == null || target == null){
			return ;
		}
		
		configuration(source, TypeDescriptor.forObject(source), target, TypeDescriptor.forObject(target));
	}
	
	public void configuration(Object source, Class<?> sourceType,
			Object target, Class<?> targetType) {
		configuration(source, TypeDescriptor.valueOf(sourceType), target, TypeDescriptor.valueOf(targetType));
	}
}
