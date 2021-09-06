package io.basc.framework.xml;

import io.basc.framework.env.Sys;

public class XmlUtils {
	private static final XmlTemplate TEMPLATE = Sys.env.getServiceLoader(XmlTemplate.class).first(() -> {
		XmlTemplate template = new XmlTemplate();
		template.configure(Sys.env);
		return template;
	});

	public static XmlTemplate getTemplate() {
		return TEMPLATE;
	}
}
