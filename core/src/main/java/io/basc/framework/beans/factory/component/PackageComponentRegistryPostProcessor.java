package io.basc.framework.beans.factory.component;

import io.basc.framework.util.ClassUtils;

public class PackageComponentRegistryPostProcessor extends LocationComponentRegistryPostProcessor {

	public PackageComponentRegistryPostProcessor(String packageName) {
		super(ClassUtils.convertClassNameToResourcePath(packageName));
	}

}
