package io.basc.framework.xml;

import io.basc.framework.beans.factory.spi.SPI;

public class XmlUtils {
	private static final XmlTemplate TEMPLATE = SPI.global().getServiceLoader(XmlTemplate.class, XmlTemplate.class)
			.getServices().first();

	public static XmlTemplate getTemplate() {
		return TEMPLATE;
	}
}
