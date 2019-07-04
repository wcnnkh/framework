package scw.core.json.support.fastjson;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public final class FastJSONUtils {
	private FastJSONUtils(){};
	
	/**
	 * 移出指定字段
	 * @param object
	 * @param excludeName 要移除的字段
	 * @return
	 */
	public static JSONObject toJsonObjectExcludeName(Object object, String... excludeName) {
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
		// 未使用PropertyFilter的方式
		if (excludeName != null) {
			for (String name : excludeName) {
				jsonObject.remove(name);
			}
		}
		return jsonObject;
	}

	/**
	 * 只保留指定字段
	 * @param object
	 * @param effectiveName 要保留的字段
	 * @return
	 */
	public static JSONObject toJsonObjectEffectiveName(Object object, String... effectiveName) {
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(object);
		JSONObject response = new JSONObject();
		if (effectiveName != null) {
			for (String name : effectiveName) {
				response.put(name, jsonObject.get(name));
			}
		}
		return response;
	}

	/**
	 * 移出指定字段
	 * @param list
	 * @param excludeName 要移除的字段
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static JSONArray toJsonArrayExcludeName(List list, String... excludeName) {
		JSONArray jsonArray = new JSONArray();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Object o = list.get(i);
				if (o == null) {
					continue;
				}

				jsonArray.add(toJsonObjectExcludeName(o, excludeName));
			}
		}
		return jsonArray;
	}

	/**
	 * 只保留指定字段
	 * @param list
	 * @param effectiveName 要保留的字段
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static JSONArray toJsonArrayEffectiveName(List list, String... effectiveName) {
		JSONArray jsonArray = new JSONArray();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				Object o = list.get(i);
				if (o == null) {
					continue;
				}

				jsonArray.add(toJsonObjectEffectiveName(o, effectiveName));
			}
		}
		return jsonArray;
	}
}
