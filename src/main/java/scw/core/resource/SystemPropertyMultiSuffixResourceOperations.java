package scw.core.resource;

import scw.core.utils.StringUtils;

public class SystemPropertyMultiSuffixResourceOperations extends AbstractMultiSuffixResourceOperations {
	private static final String CONFIG_SUFFIX = "SHUCHAOWEN_CONFIG_SUFFIX";
	private static final String RESOURCE_SUFFIX = "scw_res_suffix";
	private static volatile String defaultSuffixs;

	public static void setResourceSuffix(String suffix) {
		if (StringUtils.isEmpty(suffix)) {
			return;
		}

		defaultSuffixs = new String(suffix);
	}

	public SystemPropertyMultiSuffixResourceOperations(ResourceLookup resourceLookup) {
		super(resourceLookup);
	}

	private String getProperty(String key) {
		String v = System.getProperty(key);
		if (v == null) {
			v = System.getenv(key);
		}
		return v;
	}

	@Override
	public String[] getSuffixs() {
		if (defaultSuffixs != null) {
			return StringUtils.commonSplit(defaultSuffixs);
		}

		String value = getProperty(RESOURCE_SUFFIX);
		if (value == null) {
			value = getProperty(CONFIG_SUFFIX);
		}

		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return StringUtils.commonSplit(value);
	}

}
