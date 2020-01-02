package scw.util;

public class StringParseValueFactory extends AbstractValueFactory<String, Value> {
	public static final StringParseValueFactory STRING_PARSE_VALUE_FACTORY = new StringParseValueFactory();

	public Value get(String key) {
		return new StringValue(key);
	}

	@Override
	public Value getDefaultValue() {
		return DefaultValueDefinition.DEFAULT_VALUE_DEFINITION;
	}
}
