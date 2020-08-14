package scw.core.instance;

import java.util.Enumeration;

import scw.core.parameter.ParameterDescriptor;
import scw.util.Enumerable;

public interface InstanceBuilder<T> extends Enumerable<ParameterDescriptor[]>{
	Class<? extends T> getTargetClass();
	
	boolean isInstance();
	
	T create() throws Exception;

	T create(Object... params) throws Exception;

	T create(Class<?>[] parameterTypes, Object... params) throws Exception;
	
	/**
	 * 可用于构造该实例的参数描述组
	 * @return
	 */
	Enumeration<ParameterDescriptor[]> enumeration();
}
