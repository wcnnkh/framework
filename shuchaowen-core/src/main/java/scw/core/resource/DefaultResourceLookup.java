package scw.core.resource;

import java.io.InputStream;

import scw.core.Consumer;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;

public final class DefaultResourceLookup extends LocalResourceLookup {
	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	private static final String CLASSPATH_URL_PREFIX = "classpath:";
	private static final String CLASS_PATH_PREFIX_EL = "{classpath}";

	public DefaultResourceLookup(String workPath,
			boolean search) {
		super(workPath, search);
	}

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		if (StringUtils.isEmpty(resource)) {
			return false;
		}

		String text = SystemPropertyUtils.format(resource);
		if (StringUtils.startsWithIgnoreCase(text, CLASSPATH_URL_PREFIX)
				|| StringUtils.startsWithIgnoreCase(text, CLASS_PATH_PREFIX_EL)) {
			String eqPath = text.replaceAll("\\\\", "/");
			if (StringUtils.startsWithIgnoreCase(text, CLASSPATH_URL_PREFIX)) {
				eqPath = eqPath.substring(CLASSPATH_URL_PREFIX.length());
			} else {
				eqPath = eqPath.substring(CLASS_PATH_PREFIX_EL.length());
			}
			return super.lookup(eqPath, consumer);
		}

		if (super.lookup(text, consumer)) {
			return true;
		}

		if (text.startsWith(getWorkPath())) {
			return new FileSystemResourceLookup(false).lookup(text, consumer);
		}
		return false;
	}
}
