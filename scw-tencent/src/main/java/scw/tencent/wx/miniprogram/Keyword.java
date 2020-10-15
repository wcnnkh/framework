package scw.tencent.wx.miniprogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.json.JsonArray;
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

	public static List<Keyword> parse(JsonArray jsonArray) {
		if (jsonArray == null) {
			return null;
		}

		if (jsonArray.isEmpty()) {
			return Collections.emptyList();
		}

		List<Keyword> list = new ArrayList<Keyword>();
		for (int i = 0; i < jsonArray.size(); i++) {
			list.add(new Keyword(jsonArray.getJsonObject(i)));
		}
		return list;
	}
}
