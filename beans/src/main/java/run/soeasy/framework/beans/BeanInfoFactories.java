package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;

import lombok.NonNull;
import run.soeasy.framework.util.spi.ConfigurableServices;

public class BeanInfoFactories extends ConfigurableServices<BeanInfoFactory> implements BeanInfoProvider {
	public BeanInfoFactories() {
		setServiceClass(BeanInfoFactory.class);
	}

	/**
	 * Introspect on a Java Bean and learn about all its properties, exposed
	 * methods, and events.
	 * <p>
	 * If the BeanInfo class for a Java Bean has been previously Introspected then
	 * the BeanInfo class is retrieved from the BeanInfo cache.
	 *
	 * @param beanClass The bean class to be analyzed.
	 * @return A BeanInfo object describing the target bean.
	 * @exception IntrospectionException if an exception occurs during
	 *                                   introspection.
	 */
	@Override
	public BeanInfo getBeanInfo(@NonNull Class<?> beanClass) throws IntrospectionException {
		for (BeanInfoFactory factory : this) {
			BeanInfo beanInfo = factory.getBeanInfo(beanClass);
			if (beanInfo != null) {
				return beanInfo;
			}
		}
		return loadBeanInfo(beanClass);
	}

	protected BeanInfo loadBeanInfo(Class<?> beanClass) throws IntrospectionException {
		return Introspector.getBeanInfo(beanClass);
	}
}
