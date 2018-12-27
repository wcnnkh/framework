package scw.beans.rpc.http;

import java.nio.charset.Charset;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.AbstractBeanConfigFactory;
import scw.beans.property.PropertiesFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;

public class HttpRPCBeanConfigFactory extends AbstractBeanConfigFactory {
	private static final String TAG_NAME = "http:reference";

	public HttpRPCBeanConfigFactory(PropertiesFactory propertiesFactory, String config) throws Exception {
		Node rootNode = XmlBeanUtils.getRootNode(config);
		NodeList rootNodeList = rootNode.getChildNodes();
		for (int i = 0; i < rootNodeList.getLength(); i++) {
			Node node = rootNodeList.item(i);
			if (node == null) {
				continue;
			}

			if (!TAG_NAME.equals(node.getNodeName())) {
				continue;
			}

			String sign = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "sign");
			String packageName = XmlBeanUtils.getPackageName(propertiesFactory, node);
			String charsetName = XmlBeanUtils.getCharsetName(propertiesFactory, node, "UTF-8");
			String address = XmlBeanUtils.getAddress(propertiesFactory, node);
			if (!StringUtils.isNull(packageName)) {
				for (Class<?> clz : ClassUtils.getClasses(packageName)) {
					if (!clz.isInterface()) {
						continue;
					}

					HttpRPCBean httpRPCBean = new HttpRPCBean(clz, address, sign, Charset.forName(charsetName));
					addBean(httpRPCBean);
				}
			}

			NodeList nodeList = node.getChildNodes();
			for (int a = 0; a < nodeList.getLength(); a++) {
				Node n = nodeList.item(a);
				if (n == null) {
					continue;
				}

				String className = XmlBeanUtils.getNodeValue(propertiesFactory, node, "interface");
				if (StringUtils.isNull(className)) {
					continue;
				}

				Class<?> clz = Class.forName(className);
				String mySign = XmlBeanUtils.getNodeAttributeValue(propertiesFactory, node, "sign");
				if (StringUtils.isNull(mySign)) {
					mySign = sign;
				}

				String myScharsetName = XmlBeanUtils.getCharsetName(propertiesFactory, node, charsetName);
				String myAddress = XmlBeanUtils.getAddress(propertiesFactory, node);
				if (StringUtils.isNull(myAddress)) {
					myAddress = address;
				}
				HttpRPCBean httpRPCBean = new HttpRPCBean(clz, myAddress, mySign, Charset.forName(myScharsetName));
				addBean(httpRPCBean);
			}
		}
	}
}
