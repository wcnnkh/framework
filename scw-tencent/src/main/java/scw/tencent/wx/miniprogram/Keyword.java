package scw.tencent.wx.miniprogram;

import scw.json.JsonObject;
import scw.json.JsonObjectWrapper;

public final class Keyword extends JsonObjectWrapper {

	public Keyword(JsonObject target) {
		super(target);
	}

	public String getKeyword_id() {
		return getString("keyword_id");
	}

	public String getName() {
		return getString("name");
	}

	public String getExample() {
		return getString("example");
	}
}
