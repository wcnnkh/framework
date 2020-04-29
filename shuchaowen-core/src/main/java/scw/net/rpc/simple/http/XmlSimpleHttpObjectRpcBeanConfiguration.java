package scw.net.rpc.simple.http;

import java.util.Arrays;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.aop.DefaultFilterChain;
import scw.aop.Filter;
import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.ProxyBeanBuilder;
import scw.beans.xml.XmlBeanConfiguration;
import scw.beans.xml.XmlBeanUtils;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.io.ResourceUtils;
import scw.io.serialzer.Serializer;
import scw.io.serialzer.SerializerUtils;
import scw.util.value.property.PropertyFactory;

@Configuration(order = Integer.MIN_VALUE)
public final class XmlSimpleHttpObjectRpcBeanConfiguration extends XmlBeanConfiguration {
	private static final String TAG_NAME = "http:reference";

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		NodeList rootNodeList = getNodeList();
		if (rootNodeList == null) {
			return;
		}

		for (int i = 0; i < rootNodeList.getLength(); i++) {
			Node node = rootNodeList.item(i);
			if (node == null) {
				continue;
			}

			if (!TAG_NAME.equals(node.getNodeName())) {
				continue;
			}

			String sign = XMLUtils.getNodeAttributeValue(propertyFactory, node, "sign");
			String packageName = XmlBeanUtils.getPackageName(propertyFactory, node);
			String serializer = XMLUtils.getNodeAttributeValue(propertyFactory, node, "serializer");
			String address = XmlBeanUtils.getAddress(propertyFactory, node);
			boolean responseThrowable = StringUtils
					.parseBoolean(XMLUtils.getNodeAttributeValue(propertyFactory, node, "throwable"), true);

			Serializer ser = StringUtils.isEmpty(serializer) ? SerializerUtils.DEFAULT_SERIALIZER
					: (Serializer) beanFactory.getInstance(serializer);
			if (!StringUtils.isNull(packageName)) {
				for (Class<?> clz : ResourceUtils.getPackageScan().getClasses(packageName)) {
					if (!clz.isInterface() || AnnotationUtils.isIgnore(clz)) {
						continue;
					}

					Filter filter = new SimpleHttpObjectRpcServiceFilter(ser, sign, responseThrowable, address);
					BeanBuilder beanBuilder = new ProxyBeanBuilder(beanFactory, propertyFactory, clz, null,
							new DefaultFilterChain(Arrays.asList(filter)));
					beanDefinitions.add(new DefaultBeanDefinition(beanFactory, propertyFactory, clz, beanBuilder));
				}
			}

			NodeList nodeList = node.getChildNodes();
			for (int a = 0; a < nodeList.getLength(); a++) {
				Node n = nodeList.item(a);
				if (n == null) {
					continue;
				}

				String className = XMLUtils.getNodeAttributeValue(propertyFactory, node, "interface");
				if (StringUtils.isNull(className)) {
					continue;
				}

				Class<?> clz = ClassUtils.forName(className);
				String mySign = XMLUtils.getNodeAttributeValue(propertyFactory, node, "sign");
				if (StringUtils.isNull(mySign)) {
					mySign = sign;
				}

				String myAddress = XmlBeanUtils.getAddress(propertyFactory, node);
				if (StringUtils.isNull(myAddress)) {
					myAddress = address;
				}

				Filter filter = new SimpleHttpObjectRpcServiceFilter(ser, mySign, responseThrowable, myAddress);
				BeanBuilder beanBuilder = new ProxyBeanBuilder(beanFactory, propertyFactory, clz, null,
						new DefaultFilterChain(Arrays.asList(filter)));
				beanDefinitions.add(new DefaultBeanDefinition(beanFactory, propertyFactory, clz, beanBuilder));
			}
		}
	}
}
