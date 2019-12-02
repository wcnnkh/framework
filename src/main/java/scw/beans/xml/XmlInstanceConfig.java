package scw.beans.xml;

import java.lang.reflect.Constructor;

import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.core.instance.InstanceConfig;
import scw.core.instance.InstanceFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;

public class XmlInstanceConfig implements InstanceConfig {
	private InstanceFactory instanceFactory;
	private PropertyFactory propertyFactory;
	private Constructor<?> constructor;
	private XmlBeanParameter[] xmlBeanParameters;

	public XmlInstanceConfig(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clazz,
			XmlBeanParameter[] xmlBeanParameters) {
		this.instanceFactory = instanceFactory;
		this.propertyFactory = propertyFactory;
		if (ArrayUtils.isEmpty(xmlBeanParameters)) {
			this.constructor = ReflectionUtils.getConstructor(clazz, false);
		} else {
			for (Constructor<?> constructor : ReflectionUtils.getConstructorOrderList(clazz)	) {
				XmlBeanParameter[] beanMethodParameters = BeanUtils.sortParameters(constructor, xmlBeanParameters);
				if (beanMethodParameters != null) {
					this.xmlBeanParameters = beanMethodParameters;
					this.constructor = constructor;
					break;
				}
			}
		}

		if (constructor != null) {
			constructor.setAccessible(true);
		}
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public Object[] getArgs() {
		if (constructor == null) {
			return null;
		}

		if (ArrayUtils.isEmpty(xmlBeanParameters)) {
			return new Object[0];
		}

		try {
			return BeanUtils.getBeanMethodParameterArgs(xmlBeanParameters, instanceFactory, propertyFactory);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
