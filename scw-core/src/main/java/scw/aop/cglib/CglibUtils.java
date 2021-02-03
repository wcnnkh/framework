package scw.aop.cglib;

import java.io.Serializable;

import net.sf.cglib.proxy.Enhancer;

class CglibUtils {
	public static Enhancer createEnhancer(Class<?> clazz, Class<?>[] interfaces) {
		Enhancer enhancer = new Enhancer();
		if (Serializable.class.isAssignableFrom(clazz)) {
			enhancer.setSerialVersionUID(1L);
		}
		if (interfaces != null) {
			enhancer.setInterfaces(interfaces);
		}
		enhancer.setSuperclass(clazz);
		enhancer.setUseCache(true);
		return enhancer;
	}
}
