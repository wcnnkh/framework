package scw.beans.rpc.dubbo;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanConfigFactory;
import scw.beans.BeanFactory;
import scw.core.PropertiesFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.LoggerUtils;

public final class DubboUtils {
	private DubboUtils() {
	};

	public static boolean isSupport() {
		try {
			Class.forName("com.alibaba.dubbo.config.RegistryConfig");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean isServiceNode(Node node) {
		return "dubbo:service".equals(node.getNodeName());
	}

	public static boolean isReferenceNode(Node node) {
		return "dubbo:reference".equals(node.getNodeName());
	}

	public static boolean xmlExistDubboService(NodeList nodeList) {
		if (nodeList == null) {
			return false;
		}

		for (int x = 0; x < nodeList.getLength(); x++) {
			Node node = nodeList.item(x);
			if (node == null) {
				continue;
			}

			if (isServiceNode(node)) {
				return true;
			}
		}
		return false;
	}

	public static boolean xmlExistDubboReference(NodeList nodeList) {
		if (nodeList == null) {
			return false;
		}

		for (int x = 0; x < nodeList.getLength(); x++) {
			Node node = nodeList.item(x);
			if (node == null) {
				continue;
			}

			if (isReferenceNode(node)) {
				return true;
			}
		}
		return false;
	}

	public static BeanConfigFactory getReferenceBeanConfigFactory(BeanFactory beanFactory,
			PropertiesFactory propertiesFactory, NodeList nodeList) {
		if (!xmlExistDubboReference(nodeList)) {
			return null;
		}

		if (!isSupport()) {
			LoggerUtils.warn(DubboUtils.class, "------not support reference dubbo service------");
			return null;
		}

		return InstanceUtils.getReflectionInstanceFactory().getInstance("scw.beans.rpc.dubbo.XmlDubboBeanConfigFactory",
				beanFactory, propertiesFactory, nodeList);
	}

	public static void exportService(BeanFactory beanFactory, PropertiesFactory propertiesFactory, NodeList nodeList) {
		if (!xmlExistDubboService(nodeList)) {
			return;
		}

		if (!isSupport()) {
			LoggerUtils.warn(DubboUtils.class, "------not support export dubbo service------");
			return;
		}

		Thread thread = InstanceUtils.getReflectionInstanceFactory().getInstance(
				"scw.beans.rpc.dubbo.XmlDubboServiceExortThread", propertiesFactory, beanFactory, nodeList);
		if (thread == null) {
			return;
		}

		thread.start();
	}
}
