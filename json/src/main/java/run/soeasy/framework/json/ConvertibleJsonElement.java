package run.soeasy.framework.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;

/**
 * 通用的实现，但性能一般
 * 
 * @author soeasy.run
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode(of = "json")
final class ConvertibleJsonElement implements JsonElement {
	@NonNull
	private final JsonConverter jsonConverter;
	private final Object json;
	private JsonElement jsonArray;
	private JsonElement jsonObject;

	@Override
	public void export(Appendable target) throws IOException {
		jsonConverter.to(json, TypeDescriptor.forObject(json), target);
	}

	@Override
	public JsonArray getAsJsonArray() {
		if (isJsonArray()) {
			return this.jsonArray.getAsJsonArray();
		}
		return JsonElement.super.getAsJsonArray();
	}

	@Override
	public JsonNull getAsJsonNull() {
		if (json == null) {
			return JsonNull.INSTANCE;
		}
		return JsonElement.super.getAsJsonNull();
	}

	@Override
	public JsonObject getAsJsonObject() {
		if (isJsonObject()) {
			return jsonObject.getAsJsonObject();
		}
		return JsonElement.super.getAsJsonObject();
	}

	@Override
	public JsonPrimitive getAsJsonPrimitive() {
		return new JsonPrimitive(json);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isJsonArray() {
		if (json == null || (jsonObject != null && jsonObject.isJsonObject())) {
			return false;
		}

		if (jsonArray == null) {
			String content = jsonConverter.convert(json, String.class);
			content = content.trim();
			if ((content.startsWith("[") && content.startsWith("]"))
					|| (content.startsWith("\"[") || content.endsWith("]\""))) {
				List<Object> collection = (List<Object>) jsonConverter.convert(json,
						TypeDescriptor.collection(ArrayList.class, Object.class));
				JsonArray jsonArray = new JsonArray();
				collection.forEach((value) -> jsonArray.add(jsonConverter.toJsonElement(value)));
				this.jsonArray = jsonArray;
			}
		}

		if (jsonArray == null) {
			jsonArray = JsonNull.INSTANCE;
		}
		return jsonArray.isJsonArray();
	}

	@Override
	public boolean isJsonNull() {
		return json == null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isJsonObject() {
		if (json == null || (jsonArray != null && jsonArray.isJsonArray())) {
			return false;
		}

		if (jsonObject == null) {
			String content = jsonConverter.convert(json, String.class);
			content = content.trim();
			if ((content.startsWith("{") && content.startsWith("}"))
					|| (content.startsWith("\"{") || content.endsWith("}\""))) {
				Map<String, Object> map = (Map<String, Object>) jsonConverter.convert(json,
						TypeDescriptor.map(LinkedHashMap.class, String.class, Object.class));
				JsonObject jsonObject = new JsonObject();
				map.forEach((key, value) -> jsonObject.put(key, jsonConverter.toJsonElement(value)));
				this.jsonObject = jsonObject;
			}
		}
		if (jsonObject == null) {
			jsonObject = JsonNull.INSTANCE;
		}
		return jsonObject.isJsonObject();
	}

	@Override
	public boolean isJsonPrimitive() {
		return !(isJsonArray() || isJsonObject() || isJsonNull());
	}

	@Override
	public String toString() {
		return toJsonString();
	}
}
