package scw.application;

import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;

public final class ApplicationConfigUtils {
	private ApplicationConfigUtils() {
	};

	public static String getORMPackage(PropertyFactory propertyFactory) {
		return getPackageName(propertyFactory, "scan.orm");
	}

	public static String getServiceAnnotationPackage(PropertyFactory propertyFactory) {
		return getPackageName(propertyFactory, "scan.service");
	}

	public static String getBeanAnnotationPackage(PropertyFactory propertyFactory) {
		return getPackageName(propertyFactory, "scan.bean");
	}

	public static String getCrontabAnnotationPackage(PropertyFactory propertyFactory) {
		return getPackageName(propertyFactory, "scan.crontab");
	}

	public static String getConsumerAnnotationPackage(PropertyFactory propertyFactory) {
		return getPackageName(propertyFactory, "scan.consumer");
	}

	public static String getMQAnnotationPackage(PropertyFactory propertyFactory) {
		return getPackageName(propertyFactory, "scan.consumer");
	}

	public static String getInitStaticPackage(PropertyFactory propertyFactory) {
		return getPackageName(propertyFactory, "scan.static");
	}

	public static String getAnnotationPackage(PropertyFactory propertyFactory) {
		return getPackageName(propertyFactory, "scan.package");
	}

	public static String getPackageName(PropertyFactory propertyFactory, String configName) {
		return StringUtils.toString(propertyFactory.getProperty(configName), getRootPackage());
	}

	public static String getRootPackage() {
		return SystemPropertyUtils.getProperty("scw.root.package");
	}

	public static void setRootPackage(String packageName) {
		SystemPropertyUtils.setPrivateProperty("scw.root.package", packageName);
	}
}
