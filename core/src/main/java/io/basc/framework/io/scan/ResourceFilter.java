package io.basc.framework.io.scan;

import java.io.IOException;

import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceLoader;

public interface ResourceFilter {
	boolean match(Resource resource, ResourceLoader resourceLoader) throws IOException;
}
