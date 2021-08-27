package io.basc.framework.web.resource;

import io.basc.framework.core.Assert;
import io.basc.framework.core.utils.ArrayUtils;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.core.utils.StringUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.FileMimeTypeUitls;
import io.basc.framework.net.MimeType;
import io.basc.framework.util.AntPathMatcher;
import io.basc.framework.util.PathMatcher;
import io.basc.framework.util.StringMatchers;
import io.basc.framework.util.XUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

public abstract class AbstractStaticResourceLoader implements StaticResourceLoader {
	private static Logger logger = LoggerFactory.getLogger(StaticResourceLoader.class);
	private final TreeMap<String, TreeSet<String>> mappingMap = new TreeMap<String, TreeSet<String>>();
	private PathMatcher matcher = new AntPathMatcher();
	private String defaultFileName = "index.html";

	public PathMatcher getMatcher() {
		return matcher;
	}

	public void setMatcher(PathMatcher matcher) {
		Assert.requiredArgument(matcher != null, "matcher");
		this.matcher = matcher;
	}

	public String getDefaultFileName() {
		return defaultFileName;
	}

	public void setDefaultFileName(String defaultFileName) {
		Assert.requiredArgument(StringUtils.isNotEmpty(defaultFileName), "defaultFileName");
		this.defaultFileName = defaultFileName;
	}

	public void addMapping(String location, Collection<String> mappings) {
		Assert.requiredArgument(!CollectionUtils.isEmpty(mappings), "mappings");
		Assert.requiredArgument(location != null, "location");

		TreeSet<String> paths = mappingMap.get(location);
		if (paths == null) {
			paths = new TreeSet<String>(XUtils.getComparator(getMatcher()));
		}

		for (String mapping : mappings) {
			paths.add(mapping);
		}
		mappingMap.put(location, paths);
		logger.info("add mapping {} -> {}", location, mappings);
	}

	public final void addMapping(String location, String... mappings) {
		Assert.requiredArgument(!ArrayUtils.isEmpty(mappings) && StringUtils.isNotEmpty(mappings), "mapping");
		addMapping(location, Arrays.asList(mappings));
	}

	public MimeType getMimeType(Resource resource) {
		return FileMimeTypeUitls.getMimeType(resource);
	}

	public Resource getResource(String location) {
		if (mappingMap.isEmpty()) {
			return null;
		}

		String locationToUse = location.endsWith("/") ? (location + getDefaultFileName()) : location;
		for (Entry<String, TreeSet<String>> entry : mappingMap.entrySet()) {
			for (String path : entry.getValue()) {
				boolean accept = StringMatchers.match(matcher, path, location);
				if (accept) {
					String resourceRoot = entry.getKey();
					if (resourceRoot.endsWith("/")) {
						locationToUse = resourceRoot
								+ (locationToUse.startsWith("/") ? locationToUse.substring(1) : locationToUse);
					} else {
						locationToUse = resourceRoot
								+ (locationToUse.startsWith("/") ? locationToUse : ("/" + locationToUse));
					}

					locationToUse = StringUtils.cleanPath(locationToUse);
					return getResourceInternal(locationToUse);
				}
			}
		}
		return null;
	}

	protected abstract Resource getResourceInternal(String location);

	@Override
	public String toString() {
		return mappingMap.toString();
	}
}
