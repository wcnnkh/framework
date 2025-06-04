package run.soeasy.framework.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonArray extends ArrayList<JsonElement> implements JsonElement {
	private static final long serialVersionUID = 1L;
	public static final char PREFIX = '[';
	public static final char SUFFIX = ']';

	@Override
	public void export(Appendable target) throws IOException {
		target.append(PREFIX);
		Iterator<JsonElement> iterator = iterator();
		while (iterator.hasNext()) {
			iterator.next().export(target);
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
