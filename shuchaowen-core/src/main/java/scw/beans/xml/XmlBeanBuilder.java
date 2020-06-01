package scw.beans.xml;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.aop.MultiFilter;
import scw.beans.BeanFactory;
import scw.beans.builder.ConstructorBeanBuilder;
import scw.core.instance.AutoConstructorBuilder;
import scw.core.instance.ConstructorBuilder;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.value.property.PropertyFactory;
import scw.xml.XMLUtils;

public class XmlBeanBuilder extends ConstructorBeanBuilder {
	private volatile ConstructorBuilder constructorBuilder;

	public XmlBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass, Node beanNode)
			throws Exception {
		super(beanFactory, propertyFactory, targetClass);
		Collection<String> filterNames = getFilters(beanNode);
		if(!CollectionUtils.isEmpty(filterNames)){
			filters.add(new MultiFilter(beanFactory, getFilters(beanNode)));
		}
		
		NodeList nodeList = beanNode.getChildNodes();
		ioc.getInit()
				.getIocProcessors()
				.addAll(XmlBeanUtils.getInitMethodIocProcessors(
						getTargetClass(), nodeList));
		ioc.getDestroy()
				.getIocProcessors()
				.addAll(XmlBeanUtils.getDestroyMethodIocProcessors(
						getTargetClass(), nodeList));
		ioc.getAutowired()
				.getIocProcessors()
				.addAll(XmlBeanUtils.getBeanPropertiesIocProcessors(
						targetClass, nodeList));

		if (!getTargetClass().isInterface()) {// 可能只是映射
			XmlBeanParameter[] constructorParameters = XmlBeanUtils
					.getConstructorParameters(nodeList);
			if (ArrayUtils.isEmpty(constructorParameters)) {
				this.constructorBuilder = new AutoConstructorBuilder(
						beanFactory, propertyFactory, getTargetClass());
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
	protected ConstructorBuilder getConstructorBuilder() {
		return constructorBuilder;
	}
}
