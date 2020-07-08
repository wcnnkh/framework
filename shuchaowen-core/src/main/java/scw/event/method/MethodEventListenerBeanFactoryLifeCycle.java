package scw.event.method;

import scw.beans.BeanFactory;
import scw.beans.BeanFactoryLifeCycle;
import scw.beans.BeanUtils;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.annotation.Configuration;
import scw.event.method.annotation.RegisterMethodEventListener;
import scw.io.ResourceUtils;
import scw.value.property.PropertyFactory;

@Configuration
public class MethodEventListenerBeanFactoryLifeCycle implements
		BeanFactoryLifeCycle {

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		if (!beanFactory.isInstance(MethodEventDispatcher.class)) {
			return;
		}

		MethodEventDispatcher dispatcher = beanFactory
				.getInstance(MethodEventDispatcher.class);
		for (Class<?> clz : ResourceUtils.getPackageScan().getClasses(
				getScanAnnotationPackageName())) {
			if (MethodEventListener.class.isAssignableFrom(clz)) {
				continue;
			}

			RegisterMethodEventListener registerMethodEventListener = clz
					.getAnnotation(RegisterMethodEventListener.class);
			if (registerMethodEventListener == null) {
				continue;
			}

			MethodEventListener eventListener = (MethodEventListener) beanFactory
					.getInstance(clz);
			dispatcher.registerListener(registerMethodEventListener.value(),
					eventListener);
		}
	}

	public void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		//ignore
	}

	public String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue(
				"scw.scan.method.event.package", String.class,
				BeanUtils.getScanAnnotationPackageName());
	}

}
