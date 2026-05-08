package run.soeasy.framework.json;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.CharSequenceTemplate;

@Getter
public class JsonValue extends CharSequenceTemplate implements JsonPrimitive {
	private static final long serialVersionUID = 1L;
	private final JsonToken token;

	public JsonValue(@NonNull CharSequence value, @NonNull JsonToken token) {
		super(value);
		this.token = token;
	}

	@Override
	public boolean isCharSequence() {
		return token == JsonToken.STRING;
	}

	@Override
	public boolean isNumber() {
		return token == JsonToken.NUMBER;
	}

	@Override
	public boolean isJsonArray() {
		return token == JsonToken.BEGIN_ARRAY || token == JsonToken.END_ARRAY;
	}

	@Override
	public boolean isJsonObject() {
		return token == JsonToken.BEGIN_OBJECT || token == JsonToken.END_OBJECT;
	}

	@Override
	public boolean isJsonNull() {
		return token == JsonToken.NULL;
	}

	@Override
	public boolean isJsonPrimitive() {
		return !(isJsonArray() || isJsonObject() || isJsonNull());
	}
}