package scw.beans.xml;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.w3c.dom.Node;

import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.core.utils.StringUtils;
import scw.value.property.PropertyFactory;
import scw.xml.XMLUtils;

public final class XmlBeanDefinition extends DefaultBeanDefinition {
	private final LinkedList<String> names = new LinkedList<String>();
	private final String id;
	private final boolean singleton;

	public XmlBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Node beanNode)
			throws ClassNotFoundException, Exception {
		this(beanFactory, propertyFactory, XmlBeanUtils
				.getClass(beanNode, true), beanNode);
	}

	public XmlBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass, Node beanNode)
			throws Exception {
		super(beanFactory, propertyFactory, targetClass, new XmlBeanBuilder(
				beanFactory, propertyFactory, targetClass, beanNode));
		this.id = getId(beanNode);
		this.names.addAll(super.getNames());
		this.names.addAll(Arrays.asList(getNames(beanNode)));
		this.singleton = XmlBeanUtils.isSingleton(beanNode) ? true : super
				.isSingleton();
	}

	@Override
	public Collection<String> getNames() {
		return Collections.unmodifiableCollection(names);
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
		return StringUtils.isEmpty(id) ? XmlBeanUtils.getClassName(node, true)
				: id;
	}

	protected String[] getNames(Node node) {
		String name = XMLUtils.getNodeAttributeValue(node, "name");
		return StringUtils.isEmpty(name) ? new String[0] : StringUtils
				.commonSplit(name);
	}
}
