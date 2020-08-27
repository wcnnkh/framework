package scw.beans.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.aop.MethodInterceptor;
import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.core.instance.InstanceIterable;
import scw.core.parameter.ParameterDescriptors;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.lang.NotSupportedException;
import scw.value.property.PropertyFactory;
import scw.xml.XMLUtils;

public class XmlBeanDefinition extends DefaultBeanDefinition {
	private List<String> names = new ArrayList<String>();
	private final String id;
	private final boolean singleton;
	private final XmlParameterFactory xmlParameterFactory;
	private Iterable<? extends MethodInterceptor> filters;

	public XmlBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Node beanNode) throws Exception {
		this(beanFactory, propertyFactory, XmlBeanUtils.getClass(beanNode, true), beanNode);
	}

	public XmlBeanDefinition(BeanFactory beanFactory, PropertyFactory propertyFactory, Class<?> targetClass,
			Node beanNode) throws Exception {
		super(beanFactory, propertyFactory, targetClass);
		Collection<String> filterNames = getFilters(beanNode);
		if (!CollectionUtils.isEmpty(filterNames)) {
			this.filters = new InstanceIterable<MethodInterceptor>(beanFactory, getFilters(beanNode));
		}

		NodeList nodeList = beanNode.getChildNodes();
		ioc.getInit().getIocProcessors().addAll(XmlBeanUtils.getInitMethodIocProcessors(getTargetClass(), nodeList));
		ioc.getDestroy().getIocProcessors()
				.addAll(XmlBeanUtils.getDestroyMethodIocProcessors(getTargetClass(), nodeList));
		ioc.getDependence().getIocProcessors()
				.addAll(XmlBeanUtils.getBeanPropertiesIocProcessors(targetClass, nodeList));
		this.xmlParameterFactory = new XmlParameterFactory(beanFactory, propertyFactory,
				XmlBeanUtils.getConstructorParameters(nodeList));
		this.id = getId(beanNode);
		this.names.addAll(super.getNames());
		this.names.addAll(Arrays.asList(getNames(beanNode)));
		this.names = Arrays.asList(this.names.toArray(new String[0]));
		this.singleton = XmlBeanUtils.isSingleton(beanNode) ? true : super.isSingleton();
	}
	
	@Override
	public Iterable<? extends MethodInterceptor> getFilters() {
		return filters;
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
	public Collection<String> getNames() {
		return names;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isSingleton() {
		return singleton;
	}

	protected String getId(Node node) {
		String id = XMLUtils.getNodeAttributeValue(node, "id");
		return StringUtils.isEmpty(id) ? XmlBeanUtils.getClassName(node, true) : id;
	}

	protected String[] getNames(Node node) {
		String name = XMLUtils.getNodeAttributeValue(node, "name");
		return StringUtils.isEmpty(name) ? new String[0] : StringUtils.commonSplit(name);
	}

	@Override
	public boolean isInstance() {
		if (ArrayUtils.isEmpty(xmlParameterFactory.getXmlBeanParameters())) {
			return super.isInstance();
		}

		for (ParameterDescriptors parameterDescriptors : this) {
			if (xmlParameterFactory.isAccept(parameterDescriptors)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object create() throws Exception {
		if (ArrayUtils.isEmpty(xmlParameterFactory.getXmlBeanParameters())) {
			return super.create();
		}

		for (ParameterDescriptors parameterDescriptors : this) {
			if (xmlParameterFactory.isAccept(parameterDescriptors)) {
				return create(parameterDescriptors.getTypes(), xmlParameterFactory.getParameters(parameterDescriptors));
			}
		}
		throw new NotSupportedException(getTargetClass().getName());
	}
}
