package scw.io.resource;

import scw.core.utils.StringUtils;
import scw.util.value.property.PropertyFactory;

public class PropertyFactoryMultiSuffixResourceOperations extends AbstractMultiSuffixResourceOperations {
	private static final String CONFIG_SUFFIX = "SHUCHAOWEN_CONFIG_SUFFIX";
	private static final String RESOURCE_SUFFIX = "scw_res_suffix";
	private static volatile String defaultSuffixs;
	private PropertyFactory propertyFactory;
	
	public static void setResourceSuffix(String suffix) {
		if (StringUtils.isEmpty(suffix)) {
			return;
		}

		defaultSuffixs = new String(suffix);
	}

	public PropertyFactoryMultiSuffixResourceOperations(ResourceLookup resourceLookup, PropertyFactory propertyFactory) {
		super(resourceLookup);
		this.propertyFactory = propertyFactory;
	}

	protected String getProperty(String key) {
		return propertyFactory.getString(key);
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
