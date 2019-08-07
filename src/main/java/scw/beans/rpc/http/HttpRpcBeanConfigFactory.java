package scw.beans.rpc.http;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfigFactory;
import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertyFactory;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.io.serializer.Serializer;
import scw.io.serializer.SerializerUtils;

public final class HttpRpcBeanConfigFactory extends AbstractBeanConfigFactory {
	private static final String TAG_NAME = "http:reference";

	public HttpRpcBeanConfigFactory(BeanFactory beanFactory, PropertyFactory propertyFactory, NodeList rootNodeList,
			String[] filterNames) throws Exception {
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

			Serializer ser = StringUtils.isEmpty(serializer) ? SerializerUtils.DEFAULT_SERIALIZER
					: (Serializer) beanFactory.getInstance(serializer);
			if (!StringUtils.isNull(packageName)) {
				for (Class<?> clz : ResourceUtils.getClassList(packageName)) {
					if (!clz.isInterface()) {
						continue;
					}

					HttpRpcBean httpRpcBean = new HttpRpcBean(beanFactory, clz, address, sign, ser, filterNames);
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

				Class<?> clz = Class.forName(className);
				String mySign = XMLUtils.getNodeAttributeValue(propertyFactory, node, "sign");
				if (StringUtils.isNull(mySign)) {
					mySign = sign;
				}

				String myAddress = XmlBeanUtils.getAddress(propertyFactory, node);
				if (StringUtils.isNull(myAddress)) {
					myAddress = address;
				}

				HttpRpcBean httpRpcBean = new HttpRpcBean(beanFactory, clz, myAddress, mySign, ser, filterNames);
				addBean(httpRpcBean);
			}
		}
	}
}
