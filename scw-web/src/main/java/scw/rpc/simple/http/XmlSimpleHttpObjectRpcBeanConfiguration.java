package scw.rpc.simple.http;

import java.util.Arrays;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.aop.MethodInterceptor;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.ProxyBeanDefinition;
import scw.beans.xml.XmlBeanFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.context.annotation.Provider;
import scw.core.annotation.AnnotationUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.dom.DomUtils;
import scw.io.Serializer;
import scw.io.SerializerUtils;

@Provider(order = Integer.MIN_VALUE)
public final class XmlSimpleHttpObjectRpcBeanConfiguration implements BeanFactoryPostProcessor {
	private static final String TAG_NAME = "http:reference";
	
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		if (beanFactory instanceof XmlBeanFactory) {
			NodeList rootNodeList = ((XmlBeanFactory) beanFactory).getNodeList();
			for (int i = 0; i < rootNodeList.getLength(); i++) {
				Node node = rootNodeList.item(i);
				if (node == null) {
					continue;
				}

				if (!TAG_NAME.equals(node.getNodeName())) {
					continue;
				}

				String sign = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "sign");
				String packageName = XmlBeanUtils.getPackageName(beanFactory.getEnvironment(), node);
				String serializer = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "serializer");
				String address = XmlBeanUtils.getAddress(beanFactory.getEnvironment(), node);
				boolean responseThrowable = StringUtils
						.parseBoolean(DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "throwable"), true);

				Serializer ser = StringUtils.isEmpty(serializer) ? SerializerUtils.DEFAULT_SERIALIZER
						: (Serializer) beanFactory.getInstance(serializer);
				if (!StringUtils.isEmpty(packageName)) {
					for (Class<?> clz : beanFactory.getClassesLoader(packageName)) {
						if (!clz.isInterface() || AnnotationUtils.isIgnore(clz)) {
							continue;
						}

						MethodInterceptor filter = new SimpleHttpObjectRpcMethodInterceptor(ser, sign,
								responseThrowable, address);
						BeanDefinition beanBuilder = new ProxyBeanDefinition(beanFactory, clz,
								Arrays.asList(filter));
						beanFactory.registerDefinition(beanBuilder.getId(), beanBuilder);
					}
				}

				NodeList nodeList = node.getChildNodes();
				for (int a = 0; a < nodeList.getLength(); a++) {
					Node n = nodeList.item(a);
					if (n == null) {
						continue;
					}

					String className = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "interface");
					if (StringUtils.isEmpty(className)) {
						continue;
					}

					Class<?> clz = ClassUtils.getClass(className, beanFactory.getClassLoader());
					String mySign = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "sign");
					if (StringUtils.isEmpty(mySign)) {
						mySign = sign;
					}

					String myAddress = XmlBeanUtils.getAddress(beanFactory.getEnvironment(), node);
					if (StringUtils.isEmpty(myAddress)) {
						myAddress = address;
					}

					MethodInterceptor filter = new SimpleHttpObjectRpcMethodInterceptor(ser, mySign, responseThrowable,
							myAddress);
					BeanDefinition beanBuilder = new ProxyBeanDefinition(beanFactory, clz,
							Arrays.asList(filter));
					beanFactory.registerDefinition(beanBuilder.getId(), beanBuilder);
				}
			}
		}
	}
}
