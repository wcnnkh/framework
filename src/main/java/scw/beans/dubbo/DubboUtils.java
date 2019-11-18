package scw.beans.dubbo;

import java.lang.reflect.Method;

import org.apache.dubbo.config.DubboShutdownHook;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanConfigFactory;
import scw.beans.BeanFactory;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.instance.InstanceFactory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class DubboUtils {
	private static Logger logger = LoggerUtils.getLogger(DubboUtils.class);

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

	public static BeanConfigFactory getReferenceBeanConfigFactory(ValueWiredManager valueWiredManager,
			BeanFactory beanFactory, PropertyFactory propertyFactory, NodeList nodeList) {
		if (!xmlExistDubboReference(nodeList)) {
			return null;
		}

		if (!isSupport()) {
			logger.warn("------not support reference dubbo service------");
			return null;
		}

		return InstanceUtils.getInstance("scw.beans.dubbo.XmlDubboBeanConfigFactory", valueWiredManager, beanFactory,
				propertyFactory, nodeList);
	}

	public static void exportService(InstanceFactory beanFactory, PropertyFactory propertyFactory, NodeList nodeList) {
		if(nodeList == null){
			return ;
		}
		
		if (!xmlExistDubboService(nodeList)) {
			return;
		}

		if (!isSupport()) {
			logger.warn("------not support export dubbo service------");
			return;
		}

		Runnable runnable = InstanceUtils.getInstance("scw.beans.dubbo.XmlDubboServiceExort", propertyFactory,
				beanFactory, nodeList);
		if (runnable == null) {
			return;
		}

		runnable.run();
	}
	
	public static void registerDubboShutdownHook() {
		Class<?> dubboShutdownHook = null;
		try {
			dubboShutdownHook = Class.forName("org.apache.dubbo.config.DubboShutdownHook");
		} catch (ClassNotFoundException e1) {
		}

		if (dubboShutdownHook == null) {
			return;
		}

		DubboShutdownHook.getDubboShutdownHook().register();
		try {
			Object obj = ReflectUtils.invokeStaticMethod(dubboShutdownHook, "getDubboShutdownHook", new Class[0]);
			Method method = ReflectUtils.findMethod(dubboShutdownHook, "register");
			method.invoke(obj);
		} catch (Exception e) {
			logger.error(e, "shutdown error");
		}
	}
}
