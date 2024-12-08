package io.basc.framework.core.scan;

import java.io.IOException;

import io.basc.framework.util.io.Resource;
import io.basc.framework.util.io.load.ResourceLoader;

public interface ResourceFilter {
	boolean match(Resource resource, ResourceLoader resourceLoader) throws IOException;
}
