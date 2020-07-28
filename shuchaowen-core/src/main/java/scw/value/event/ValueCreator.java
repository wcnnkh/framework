package scw.value.event;

import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.Value;

public interface ValueCreator {
	static final ValueCreator CREATOR = new ValueCreator() {

		public Value create(String key, Object value) {
			if (value instanceof String) {
				return new StringValue((String) value);
			} else {
				return new AnyValue(value);
			}
		}
	};

	Value create(String key, Object value);
}
