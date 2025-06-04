package run.soeasy.framework.json;

import java.io.IOException;

public class JsonNull implements JsonElement {
	@Override
	public void export(Appendable target) throws IOException {
		target.append("null");
	}
	
	@Override
	public String toString() {
		return toJsonString();
	}
}
