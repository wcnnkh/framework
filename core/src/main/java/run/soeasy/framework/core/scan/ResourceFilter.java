package run.soeasy.framework.core.scan;

import java.io.IOException;

import run.soeasy.framework.util.io.Resource;
import run.soeasy.framework.util.io.load.ResourceLoader;

public interface ResourceFilter {
	boolean match(Resource resource, ResourceLoader resourceLoader) throws IOException;
}
