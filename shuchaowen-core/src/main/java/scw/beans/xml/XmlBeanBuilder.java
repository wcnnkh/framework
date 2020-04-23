package scw.beans.xml;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.ConstructorBeanBuilder;
import scw.beans.BeanFactory;
import scw.core.instance.AutoConstructorBuilder;
import scw.core.instance.ConstructorBuilder;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.util.value.property.PropertyFactory;

public class XmlBeanBuilder extends ConstructorBeanBuilder {
	private final XmlBeanParameter[] properties;
	private volatile ConstructorBuilder constructorBuilder;

	public XmlBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass, Node beanNode)
			throws Exception {
		super(beanFactory, propertyFactory, targetClass);
		filterNames.addAll(getFilters(beanNode));
		NodeList nodeList = beanNode.getChildNodes();
		initMethods.addAll(XmlBeanUtils.getInitMethodList(getTargetClass(),
				nodeList));
		this.destroyMethods.addAll(XmlBeanUtils.getDestroyMethodList(
				getTargetClass(), nodeList));
		this.properties = XmlBeanUtils.getBeanProperties(nodeList);

		if (!getTargetClass().isInterface()) {// 可能只是映射
			XmlBeanParameter[] constructorParameters = XmlBeanUtils
					.getConstructorParameters(nodeList);
			if (ArrayUtils.isEmpty(constructorParameters)) {
				this.constructorBuilder = new AutoConstructorBuilder(
						beanFactory, propertyFactory, getTargetClass(),
						ParameterUtils.getParameterDescriptorFactory());
			} else {
				this.constructorBuilder = new XmlConstructorBuilder(
						beanFactory, propertyFactory, getTargetClass(),
						constructorParameters);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Collection<String> getFilters(Node node) {
		String filters = XMLUtils.getNodeAttributeValue(node, "filters");
		if (StringUtils.isEmpty(filters)) {
			return Collections.EMPTY_LIST;
		}

		return Arrays.asList(StringUtils.commonSplit(filters));
	}

	@Override
	public void init(Object instance) throws Exception {
		if (!ArrayUtils.isEmpty(properties)) {
			for (XmlBeanParameter beanProperties : properties) {
				Field field = ReflectionUtils.getField(getTargetClass(),
						beanProperties.getDisplayName(), true);
				if (field == null) {
					continue;
				}

				ReflectionUtils.setFieldValue(getTargetClass(), field,
						instance, beanProperties.parseValue(beanFactory,
								propertyFactory, field.getGenericType()));
			}
		}
		super.init(instance);
	}

	@Override
	protected ConstructorBuilder getConstructorBuilder() {
		return constructorBuilder;
	}
}
