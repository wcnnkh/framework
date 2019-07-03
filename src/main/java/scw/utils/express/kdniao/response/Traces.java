package scw.utils.express.kdniao.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import scw.core.utils.CollectionUtils;
import scw.json.JSONArray;
import scw.json.JSONObject;

/**
 * 快递鸟物流轨迹
 * 
 * @author asus1
 *
 */
public class Traces implements Serializable {
	private static final long serialVersionUID = 1L;
	private String acceptTime;// 时间
	private String acceptStation;// 描述
	private String remark;// 备注

	// 用于序列化
	Traces() {
	};

	public Traces(String acceptTime, String acceptStation, String remark) {
		this.acceptStation = acceptStation;
		this.acceptTime = acceptTime;
		this.remark = remark;
	}

	public Traces(JSONObject json) {
		this(json.getString("AcceptTime"), json.getString("AcceptStation"),
				json.getString("Remark"));
	}

	public final String getAcceptTime() {
		return acceptTime;
	}

	public final String getAcceptStation() {
		return acceptStation;
	}

	public final String getRemark() {
		return remark;
	}

	public static List<Traces> parseTraces(JSONArray jsonArray) {
		if (CollectionUtils.isEmpty(jsonArray)) {
			return null;
		}

		List<Traces> list = new ArrayList<Traces>(jsonArray.size());
		for (int i = 0, size = jsonArray.size(); i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject == null) {
				continue;
			}

			list.add(new Traces(jsonObject));
		}
		return list;
	}
}
