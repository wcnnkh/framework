package scw.core.aop;

import java.util.Collection;

import net.sf.cglib.proxy.Enhancer;

public final class CglibUtils {
	private CglibUtils() {
	};

	public static Enhancer createEnhancer(Class<?> type, Collection<Filter> filters) {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(type.getInterfaces());
		enhancer.setCallback(new FiltersConvertCglibMethodInterceptor(filters));
		enhancer.setSuperclass(type);
		return enhancer;
	}
}
