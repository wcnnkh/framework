package scw.util.value.property;

import scw.core.StringFormat;
import scw.util.FormatUtils;
import scw.util.value.AbstractValueFactory;
import scw.util.value.DefaultValueDefinition;
import scw.util.value.Value;

public abstract class AbstractPropertyFactory extends
		AbstractValueFactory<String> implements PropertyFactory {

	@Override
	protected Value getDefaultValue(String key) {
		return DefaultValueDefinition.DEFAULT_VALUE_DEFINITION;
	}

	public String format(String text, boolean supportEL) {
		return FormatUtils.format(text, this, supportEL);
	}

	public String format(String text, String prefix, String suffix) {
		return StringFormat.format(text, prefix, suffix, this);
	}
}
