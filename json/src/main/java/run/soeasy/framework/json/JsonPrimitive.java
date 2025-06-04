package run.soeasy.framework.json;

import java.io.IOException;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.Value;
import run.soeasy.framework.core.math.BigDecimalValue;
import run.soeasy.framework.core.math.IntValue;
import run.soeasy.framework.core.math.LongValue;
import run.soeasy.framework.core.math.NumberValue;

@RequiredArgsConstructor
public class JsonPrimitive implements JsonElement, Value {
	@NonNull
	private final Object value;

	@Override
	public void export(Appendable target) throws IOException {
		String str = JsonElement.escaping(getAsString());
		if (this.value instanceof String) {
			target.append('"');
			target.append(str);
			target.append('"');
		} else {
			target.append(str);
		}
	}

	@Override
	public NumberValue getAsNumber() {
		if (value instanceof NumberValue) {
			return (NumberValue) value;
		}

		if (value instanceof Long) {
			return new LongValue((long) value);
		}

		if (value instanceof Integer) {
			return new IntValue((int) value);
		}

		return value == null ? null : new BigDecimalValue(String.valueOf(value));
	}

	@Override
	public String getAsString() {
		if (value instanceof NumberValue) {
			return ((NumberValue) value).getAsString();
		}
		return String.valueOf(value);
	}

	@Override
	public boolean isNumber() {
		return value instanceof Number;
	}

	@Override
	public String toString() {
		return toJsonString();
	}
}
