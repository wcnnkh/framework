package scw.beans.xml;

import java.lang.reflect.Constructor;

import scw.core.instance.ConstructorBuilder;
import scw.core.instance.InstanceFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.value.property.PropertyFactory;

public class XmlConstructorBuilder implements ConstructorBuilder {
	private final InstanceFactory instanceFactory;
	private final PropertyFactory propertyFactory;
	private volatile XmlConstructorDescriptor constructorDescriptor;
	private final XmlBeanParameter[] xmlBeanParameters;
	private final Class<?> clazz;

	public XmlConstructorBuilder(InstanceFactory instanceFactory, PropertyFactory propertyFactory, Class<?> clazz,
			XmlBeanParameter[] xmlBeanParameters) {
		this.instanceFactory = instanceFactory;
		this.propertyFactory = propertyFactory;
		this.xmlBeanParameters = xmlBeanParameters;
		this.clazz = clazz;
	}

	public XmlConstructorDescriptor getXmlConstructorDescriptor() {
		if (constructorDescriptor == null) {
			synchronized (this) {
				if (constructorDescriptor == null) {
					for (Constructor<?> constructor : ReflectionUtils.getConstructorOrderList(clazz)) {
						XmlBeanParameter[] beanMethodParameters = XmlBeanUtils.sortParameters(constructor,
								xmlBeanParameters);
						if (beanMethodParameters != null) {
							this.constructorDescriptor = new XmlConstructorDescriptor(constructor,
									beanMethodParameters);
							break;
						}
					}

					if (constructorDescriptor == null) {
						this.constructorDescriptor = new XmlConstructorDescriptor(null, null);
					}
				}
			}
		}
		return constructorDescriptor;
	}

	public Constructor<?> getConstructor() {
		return getXmlConstructorDescriptor().getConstructor();
	}

	public Object[] getArgs() throws Exception {
		Constructor<?> constructor = getConstructor();
		if (constructor == null) {
			return null;
		}

		if (ArrayUtils.isEmpty(getXmlConstructorDescriptor().getXmlBeanParameters())) {
			return new Object[0];
		}

		return XmlBeanUtils.getBeanMethodParameterArgs(getXmlConstructorDescriptor().getXmlBeanParameters(),
				instanceFactory, propertyFactory);
	}

	private static final class XmlConstructorDescriptor {
		private final Constructor<?> constructor;
		private final XmlBeanParameter[] xmlBeanParameters;

		public XmlConstructorDescriptor(Constructor<?> constructor, XmlBeanParameter[] xmlBeanParameters) {
			this.constructor = constructor;
			this.xmlBeanParameters = xmlBeanParameters;
		}

		public Constructor<?> getConstructor() {
			return constructor;
		}

		public XmlBeanParameter[] getXmlBeanParameters() {
			return xmlBeanParameters;
		}
	}
}
