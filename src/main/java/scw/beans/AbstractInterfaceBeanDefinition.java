package scw.beans;

import scw.core.Init;
import scw.core.exception.NotSupportException;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;

public abstract class AbstractInterfaceBeanDefinition implements BeanDefinition {
	private final Class<?> interfaceClass;
	private final String id;
	private final NoArgumentBeanMethod[] initMethods;
	private final NoArgumentBeanMethod[] destroyMethods;
	private final String[] names;

	public AbstractInterfaceBeanDefinition(Class<?> interfaceClass) {
		this.interfaceClass = interfaceClass;
		this.id = ClassUtils.getProxyRealClassName(interfaceClass);
		this.names = AnnotationBeanDefinition.getServiceNames(interfaceClass);
		this.initMethods = AnnotationBeanDefinition.getInitMethodList(
				interfaceClass).toArray(new NoArgumentBeanMethod[0]);
		this.destroyMethods = AnnotationBeanDefinition.getDestroyMethdoList(
				interfaceClass).toArray(new NoArgumentBeanMethod[0]);
	}

	public final Class<?> getInterfaceClass() {
		return interfaceClass;
	}

	public boolean isSingleton() {
		return true;
	}

	public boolean isProxy() {
		return true;
	}

	public String getId() {
		return this.id;
	}

	public Class<?> getType() {
		return interfaceClass;
	}
	
	public boolean isInstance() {
		return true;
	}

	public <T> T create(Object... params) {
		throw new NotSupportException(getType().getName());
	}
	
	public <T> T create(Class<?>[] parameterTypes, Object... params) {
		throw new NotSupportException(getType().getName());
	}

	public void autowrite(Object bean) throws Exception {
		// ignore
	}

	public void init(Object bean) throws Exception {
		if (ArrayUtils.isEmpty(initMethods)) {
			for (NoArgumentBeanMethod method : initMethods) {
				method.invoke(bean);
			}
		}

		if (bean instanceof Init) {
			((Init) bean).init();
		}
	}

	public void destroy(Object bean) throws Exception {
		if (ArrayUtils.isEmpty(destroyMethods)) {
			for (NoArgumentBeanMethod method : destroyMethods) {
				method.invoke(bean);
			}
		}

		if (bean instanceof scw.core.Destroy) {
			((scw.core.Destroy) bean).destroy();
		}
	}

	public String[] getNames() {
		return names;
	}
}
