package scw.beans.rpc.http;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfigFactory;
import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.core.Constants;
import scw.core.PropertiesFactory;
import scw.core.serializer.Serializer;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public final class HttpRpcBeanConfigFactory extends AbstractBeanConfigFactory {
	private static final String TAG_NAME = "http:reference";

	public HttpRpcBeanConfigFactory(BeanFactory beanFactory, PropertiesFactory propertiesFactory, NodeList rootNodeList)
			throws Exception {
		for (int i = 0; i < rootNodeList.getLength(); i++) {
			Node node = rootNodeList.item(i);
			if (node == null) {
				continue;
			}

			if (!TAG_NAME.equals(node.getNodeName())) {
				continue;
			}

			String sign = XMLUtils.getNodeAttributeValue(propertiesFactory, node, "sign");
			String packageName = XmlBeanUtils.getPackageName(propertiesFactory, node);
			String serializer = XMLUtils.getNodeAttributeValue(propertiesFactory, node, "serializer");
			String address = XmlBeanUtils.getAddress(propertiesFactory, node);

			Serializer ser = StringUtils.isEmpty(serializer) ? Constants.DEFAULT_SERIALIZER
					: (Serializer) beanFactory.getInstance(serializer);
			if (!StringUtils.isNull(packageName)) {
				for (Class<?> clz : ClassUtils.getClasses(packageName)) {
					if (!clz.isInterface()) {
						continue;
					}

					HttpRpcBean httpRpcBean = new HttpRpcBean(beanFactory, clz, address, sign, ser);
					addBean(httpRpcBean);
				}
			}

			NodeList nodeList = node.getChildNodes();
			for (int a = 0; a < nodeList.getLength(); a++) {
				Node n = nodeList.item(a);
				if (n == null) {
					continue;
				}

				String className = XMLUtils.getNodeAttributeValue(propertiesFactory, node, "interface");
				if (StringUtils.isNull(className)) {
					continue;
				}

				Class<?> clz = Class.forName(className);
				String mySign = XMLUtils.getNodeAttributeValue(propertiesFactory, node, "sign");
				if (StringUtils.isNull(mySign)) {
					mySign = sign;
				}

				String myAddress = XmlBeanUtils.getAddress(propertiesFactory, node);
				if (StringUtils.isNull(myAddress)) {
					myAddress = address;
				}

				HttpRpcBean httpRpcBean = new HttpRpcBean(beanFactory, clz, myAddress, mySign, ser);
				addBean(httpRpcBean);
			}
		}
	}
}
