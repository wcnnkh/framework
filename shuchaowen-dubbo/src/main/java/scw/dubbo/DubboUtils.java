package scw.dubbo;

import java.lang.reflect.Method;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public final class DubboUtils {
	private static Logger logger = LoggerUtils.getLogger(DubboUtils.class);

	private DubboUtils() {
	};

	public static boolean isSupport() {
		return ClassUtils.isPresent("org.apache.dubbo.config.annotation.Service");
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

	public static void registerDubboShutdownHook() {
		Class<?> dubboShutdownHook = null;
		try {
			dubboShutdownHook = ClassUtils.forName("org.apache.dubbo.config.DubboShutdownHook");
		} catch (ClassNotFoundException e1) {
		}

		if (dubboShutdownHook == null) {
			return;
		}

		try {
			Object obj = ReflectionUtils.invokeStaticMethod(dubboShutdownHook, "getDubboShutdownHook", new Class[0]);
			Method method = ReflectionUtils.findMethod(dubboShutdownHook, "register");
			method.invoke(obj);
		} catch (Exception e) {
			logger.error(e, "shutdown error");
		}
	}
}
