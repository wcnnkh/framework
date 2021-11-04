package io.basc.framework.xml;

import io.basc.framework.env.Sys;

public class XmlUtils {
	private static final XmlTemplate TEMPLATE = Sys.env.getServiceLoader(XmlTemplate.class, XmlTemplate.class).first();

	public static XmlTemplate getTemplate() {
		return TEMPLATE;
	}
}
