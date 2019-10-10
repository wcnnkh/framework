package scw.beans.dubbo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanConfigFactory;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.logger.LoggerUtils;

public final class DubboUtils {
	private DubboUtils() {
	};

	public static boolean isSupport() {
		try {
			Class.forName("org.apache.dubbo.config.annotation.Service");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	public static boolean isServiceNode(Node node) {
		return "dubbo:service".equalsIgnoreCase(node.getNodeName());
	}

	public static boolean isReferenceNode(Node node) {
		return "dubbo:reference".equalsIgnoreCase(node.getNodeName());
	}

	public static boolean isApplicationNode(Node node) {
		return "dubbo:application".equalsIgnoreCase(node.getNodeName());
	}

	public static boolean isMetadataReportNode(Node node) {
		return "dubbo:metadata-report".equalsIgnoreCase(node.getNodeName());
	}

	public static boolean isConfigCenterNode(Node node) {
		return "dubbo:config-center".equalsIgnoreCase(node.getNodeName());
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

	public static BeanConfigFactory getReferenceBeanConfigFactory(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, NodeList nodeList) {
		if (!xmlExistDubboReference(nodeList)) {
			return null;
		}

		if (!isSupport()) {
			LoggerUtils.warn(DubboUtils.class, "------not support reference dubbo service------");
			return null;
		}

		return InstanceUtils.getInstance("scw.beans.dubbo.XmlDubboBeanConfigFactory", valueWiredManager, beanFactory, propertyFactory,
				nodeList);
	}

	public static void exportService(InstanceFactory beanFactory, PropertyFactory propertyFactory, NodeList nodeList) {
		if (!xmlExistDubboService(nodeList)) {
			return;
		}

		if (!isSupport()) {
			LoggerUtils.warn(DubboUtils.class, "------not support export dubbo service------");
			return;
		}

		Runnable runnable = InstanceUtils.getInstance("scw.beans.dubbo.XmlDubboServiceExort", propertyFactory,
				beanFactory, nodeList);
		if (runnable == null) {
			return;
		}

		runnable.run();
	}

	public static void destoryAll() {
		try {
			Class<?> clz = Class.forName("com.alibaba.dubbo.config.ProtocolConfig");
			Method method = clz.getMethod("destroyAll");
			if (Modifier.isStatic(method.getModifiers())) {
				method.invoke(null);
			}
		} catch (ClassNotFoundException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
