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
import scw.beans.support.DefaultBeanDefinition;
import scw.core.parameter.ParameterDescriptors;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.dom.DomUtils;
import scw.instance.support.InstanceIterable;
import scw.lang.NotSupportedException;

public class XmlBeanDefinition extends DefaultBeanDefinition {
	private List<String> names = new ArrayList<String>();
	private final String id;
	private final Boolean singleton;
	private final XmlParameterFactory xmlParameterFactory;
	private Iterable<? extends MethodInterceptor> filters;

	public XmlBeanDefinition(BeanFactory beanFactory, Node beanNode) throws Exception {
		this(beanFactory, XmlBeanUtils.getClass(beanNode, true, beanFactory.getClassLoader()), beanNode);
	}

	public XmlBeanDefinition(BeanFactory beanFactory, Class<?> targetClass,
			Node beanNode) throws Exception {
		super(beanFactory, targetClass);
		Collection<String> filterNames = getFilters(beanNode);
		if (!CollectionUtils.isEmpty(filterNames)) {
			this.filters = new InstanceIterable<MethodInterceptor>(beanFactory, getFilters(beanNode));
		}

		NodeList nodeList = beanNode.getChildNodes();
		ioc.getInit().getIocProcessors().addAll(XmlBeanUtils.getInitMethodIocProcessors(getTargetClass(), nodeList, beanFactory.getClassLoader()));
		ioc.getDestroy().getIocProcessors()
				.addAll(XmlBeanUtils.getDestroyMethodIocProcessors(getTargetClass(), nodeList, beanFactory.getClassLoader()));
		ioc.getDependence().getIocProcessors()
				.addAll(XmlBeanUtils.getBeanPropertiesIocProcessors(targetClass, nodeList, beanFactory.getClassLoader()));
		this.xmlParameterFactory = new XmlParameterFactory(beanFactory,
				XmlBeanUtils.getConstructorParameters(nodeList, beanFactory.getClassLoader()));
		this.id = getId(beanNode);
		this.names.addAll(super.getNames());
		this.names.addAll(Arrays.asList(getNames(beanNode)));
		this.names = Arrays.asList(this.names.toArray(new String[0]));
		this.singleton = XmlBeanUtils.isSingleton(beanNode);
	}
	
	@Override
	public Iterable<? extends MethodInterceptor> getFilters() {
		return filters;
	}

	@SuppressWarnings("unchecked")
	protected Collection<String> getFilters(Node node) {
		String filters = DomUtils.getNodeAttributeValue(node, "filters");
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
		return singleton == null? super.isSingleton() : singleton;
	}

	protected String getId(Node node) {
		String id = DomUtils.getNodeAttributeValue(node, "id");
		return StringUtils.isEmpty(id) ? XmlBeanUtils.getClassName(node, true) : id;
	}

	protected String[] getNames(Node node) {
		String name = DomUtils.getNodeAttributeValue(node, "name");
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
