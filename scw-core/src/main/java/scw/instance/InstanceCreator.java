package scw.instance;

import scw.util.Creator;

public interface InstanceCreator<T> extends Creator<T>{
	T create() throws InstanceException;
	
	T create(Class<?>[] parameterTypes, Object[] params) throws InstanceException;
}
