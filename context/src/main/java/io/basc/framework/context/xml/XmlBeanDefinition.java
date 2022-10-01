package io.basc.framework.context.xml;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.support.UnmodifiableMethodInterceptors;
import io.basc.framework.context.Context;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.factory.support.InstanceIterable;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

public class XmlBeanDefinition extends FactoryBeanDefinition {
	private static Logger logger = LoggerFactory.getLogger(XmlBeanDefinition.class);
	private final XmlParametersFactory xmlParameterFactory;

	public XmlBeanDefinition(Context context, Node beanNode) throws Exception {
		this(context, XmlBeanUtils.getClass(beanNode, true, context.getClassLoader()), beanNode);
	}

	public XmlBeanDefinition(Context context, Class<?> targetClass, Node beanNode) throws Exception {
		super(context, targetClass);
		Collection<String> filterNames = getFilters(beanNode);
		if (!CollectionUtils.isEmpty(filterNames)) {
			getMethodInterceptors().addMethodInterceptor(
					new UnmodifiableMethodInterceptors(new InstanceIterable<MethodInterceptor>(context, filterNames)));
		}

		NodeList nodeList = beanNode.getChildNodes();
		getInitProcessors().addServices(XmlBeanUtils.getInitMethodIocProcessors(context, targetClass, nodeList));
		getDestroyProcessors().addServices(XmlBeanUtils.getDestroyMethodIocProcessors(context, targetClass, nodeList));
		getDependenceProcessors()
				.addServices(XmlBeanUtils.getBeanPropertiesIocProcessors(context, targetClass, nodeList));
		this.xmlParameterFactory = new XmlParametersFactory(context,
				XmlBeanUtils.getConstructorParameters(nodeList, context.getClassLoader()));
		setId(getId(beanNode));
		Set<String> names = new LinkedHashSet<String>();
		names.addAll(super.getNames());
		names.addAll(Arrays.asList(getNames(beanNode)));
		setNames(names);
		setSingleton(XmlBeanUtils.isSingleton(beanNode));
	}

	@SuppressWarnings("unchecked")
	protected Collection<String> getFilters(Node node) {
		String filters = DomUtils.getNodeAttributeValue(node, "filters");
		if (StringUtils.isEmpty(filters)) {
			return Collections.EMPTY_LIST;
		}

		return Arrays.asList(StringUtils.splitToArray(filters));
	}

	protected String getId(Node node) {
		String id = DomUtils.getNodeAttributeValue(node, "id");
		return StringUtils.isEmpty(id) ? XmlBeanUtils.getClassName(node, true) : id;
	}

	protected String[] getNames(Node node) {
		String name = DomUtils.getNodeAttributeValue(node, "name");
		return StringUtils.isEmpty(name) ? new String[0] : StringUtils.splitToArray(name);
	}

	private final AtomicBoolean error = new AtomicBoolean();

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

		if (!error.get() && error.compareAndSet(false, true)) {
			logger.error("not found {} accept parameters {}", this,
					Arrays.toString(xmlParameterFactory.getXmlBeanParameters()));
		}
		return false;
	}

	@Override
	public Object create() throws BeansException {
		if (ArrayUtils.isEmpty(xmlParameterFactory.getXmlBeanParameters())) {
			return super.create();
		}

		for (ParameterDescriptors parameterDescriptors : this) {
			if (xmlParameterFactory.isAccept(parameterDescriptors)) {
				return create(parameterDescriptors.getTypes(), xmlParameterFactory.getParameters(parameterDescriptors));
			}
		}
		throw new NotSupportedException(getTypeDescriptor().getName());
	}
}
