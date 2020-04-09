package scw.mvc.rpc.support;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfiguration;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.beans.xml.XmlBeanConfiguration;
import scw.beans.xml.XmlBeanUtils;
import scw.core.annotation.AnnotationUtils;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.serializer.Serializer;
import scw.serializer.SerializerUtils;
import scw.util.value.property.PropertyFactory;

@Configuration
public final class HttpRpcBeanConfiguration extends AbstractBeanConfiguration implements XmlBeanConfiguration {
	private static final String TAG_NAME = "http:reference";

	public void init(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory,
			NodeList rootNodeList) throws Exception {
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
			String[] shareHeaders = StringUtils
					.commonSplit(XMLUtils.getNodeAttributeValue(propertyFactory, node, "headers"));

			Serializer ser = StringUtils.isEmpty(serializer) ? SerializerUtils.DEFAULT_SERIALIZER
					: (Serializer) beanFactory.getInstance(serializer);
			if (!StringUtils.isNull(packageName)) {
				for (Class<?> clz : ClassUtils.getClassSet(packageName)) {
					if (!clz.isInterface() || AnnotationUtils.isIgnore(clz)) {
						continue;
					}

					HttpRpcBean httpRpcBean = new HttpRpcBean(valueWiredManager, beanFactory, propertyFactory, clz,
							address, sign, ser, responseThrowable, shareHeaders);
					addBean(httpRpcBean);
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

				addBean(new HttpRpcBean(valueWiredManager, beanFactory, propertyFactory, clz, myAddress, mySign, ser,
						responseThrowable, shareHeaders));
			}
		}
	}
}
