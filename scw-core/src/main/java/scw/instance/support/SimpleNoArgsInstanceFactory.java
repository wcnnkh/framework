package scw.instance.support;

import scw.core.reflect.ReflectionUtils;
import scw.util.XUtils;


public class SimpleNoArgsInstanceFactory extends AbstractNoArgsInstanceFactory{

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return ReflectionUtils.newInstance(clazz);
	}

	@Override
	public boolean isInstance(Class<?> clazz) {
		if(!XUtils.isAvailable(clazz)){
			return false;
		}
		return ReflectionUtils.isInstance(clazz);
	}

}
