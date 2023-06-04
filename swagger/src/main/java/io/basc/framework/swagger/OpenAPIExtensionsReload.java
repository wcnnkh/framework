package io.basc.framework.swagger;

import java.util.ArrayList;
import java.util.List;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.swagger.v3.jaxrs2.DefaultParameterExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtension;
import io.swagger.v3.jaxrs2.ext.OpenAPIExtensions;

public class OpenAPIExtensionsReload {

	public static void reload(ServiceLoaderFactory serviceLoaderFactory) {
		List<OpenAPIExtension> list = new ArrayList<>();
		list.addAll(serviceLoaderFactory.getServiceLoader(OpenAPIExtension.class).toList());
		list.add(new DefaultParameterExtension());
		OpenAPIExtensions.setExtensions(list);
	}
}
