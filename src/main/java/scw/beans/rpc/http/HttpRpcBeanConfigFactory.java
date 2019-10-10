package scw.beans.rpc.http;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfigFactory;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.beans.xml.XmlBeanUtils;
import scw.core.PropertyFactory;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;
import scw.io.SerializerUtils;
import scw.io.serializer.Serializer;

public final class HttpRpcBeanConfigFactory extends AbstractBeanConfigFactory {
	private static final String TAG_NAME = "http:reference";

	public HttpRpcBeanConfigFactory(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, NodeList rootNodeList) throws Exception {
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
				for (Class<?> clz : ResourceUtils.getClassList(packageName)) {
					if (!clz.isInterface()) {
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

				Class<?> clz = Class.forName(className);
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
