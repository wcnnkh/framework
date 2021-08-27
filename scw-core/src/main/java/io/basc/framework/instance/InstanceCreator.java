package io.basc.framework.instance;

import io.basc.framework.util.Creator;

public interface InstanceCreator<T> extends Creator<T, InstanceException>{
	T create() throws InstanceException;
	
	/**
	  *  根据参数类型和参数创建一个对象
	 * @param parameterTypes
	 * @param params
	 * @return
	 * @throws InstanceException
	 */
	T create(Class<?>[] parameterTypes, Object[] params) throws InstanceException;
}
