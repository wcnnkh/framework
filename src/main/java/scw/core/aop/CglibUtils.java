package scw.core.aop;

import java.util.Collection;

import net.sf.cglib.proxy.Enhancer;
import scw.core.ClassInfo;
import scw.core.utils.ClassUtils;

public final class CglibUtils {
	private CglibUtils() {
	};

	public static Enhancer createEnhancer(Class<?> type, Collection<Filter> filters) {
		ClassInfo classInfo = ClassUtils.getClassInfo(type);
		Enhancer enhancer = new Enhancer();

		enhancer.setInterfaces(type.getInterfaces());
		if (classInfo.getSerialVersionUID() != null) {
			enhancer.setSerialVersionUID(classInfo.getSerialVersionUID());
		}

		enhancer.setCallback(new FiltersConvertCglibMethodInterceptor(filters));
		enhancer.setSuperclass(type);
		return enhancer;
	}
}
