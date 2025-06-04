package run.soeasy.framework.json;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class JsonObject extends LinkedHashMap<String, JsonElement> implements JsonElement {
	private static final long serialVersionUID = 1L;
	public static final char PREFIX = '{';
	public static final char SUFFIX = '}';

	@Override
	public void export(Appendable target) throws IOException {
		target.append(PREFIX);
		Iterator<Entry<String, JsonElement>> iterator = entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, JsonElement> entry = iterator.next();
			target.append('"');
			target.append(JsonElement.escaping(entry.getKey()));
			target.append('"');
			target.append(':');
			entry.getValue().export(target);
			if (iterator.hasNext()) {
				target.append(',');
				target.append(' ');
			}
		}
		target.append(SUFFIX);
	}

	@Override
	public String toString() {
		return toJsonString();
	}
}
