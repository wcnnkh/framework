package scw.kdniao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.json.JSONUtils;

public class Push101Data implements Serializable {
	private static final long serialVersionUID = 1L;
	private String businessId;
	private String pushTime;
	private int count;
	private List<Data> data;

	// 序列化
	Push101Data() {
	};

	public Push101Data(String jsonData) {
		if (jsonData == null) {
			return;
		}

		JsonObject jsonObject = JSONUtils.parseObject(jsonData);
		if (jsonObject == null) {
			return;
		}

		this.businessId = jsonObject.getString("EBusinessID");
		this.pushTime = jsonObject.getString("PushTime");
		this.count = jsonObject.getIntValue("Count");
		JsonArray array = jsonObject.getJsonArray("data");
		if (array != null) {
			List<Data> list = new ArrayList<Push101Data.Data>(array.size());
			for (int i = 0, len = array.size(); i < len; i++) {
				JsonObject json = array.getJsonObject(i);
				if (json == null) {
					continue;
				}

				list.add(new Data(json));
			}
			this.data = list;
		}
	}

	public String getBusinessId() {
		return businessId;
	}

	public void setBusinessId(String businessId) {
		this.businessId = businessId;
	}

	public String getPushTime() {
		return pushTime;
	}

	public void setPushTime(String pushTime) {
		this.pushTime = pushTime;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<Data> getData() {
		return data;
	}

	public void setData(List<Data> data) {
		this.data = data;
	}

	public static class Data extends KDNiaoResponse {
		private static final long serialVersionUID = 1L;
		private String shipperCode;
		// 快递公司编码
		private String logisticCode;
		// 物流状态: 0-无轨迹，1-已揽收，2-在途中，3-签收,4-问题件
		private String state;
		// 订阅接口的Bk值
		private String callBack;
		// 预计到达时间yyyy-mm-dd
		private String stimatedDeliveryTime;
		private List<Traces> traces;

		// 序列化
		Data() {
			super(null);
		}

		public Data(JsonObject json) {
			super(json);
			this.shipperCode = json.getString("ShipperCode");
			this.logisticCode = json.getString("LogisticCode");
			this.state = json.getString("State");
			this.callBack = json.getString("CallBack");
			this.traces = Traces.parseTraces(json.getJsonArray("Traces"));
			this.stimatedDeliveryTime = json.getString("EstimatedDeliveryTime");
		}

		public String getShipperCode() {
			return shipperCode;
		}

		public String getLogisticCode() {
			return logisticCode;
		}

		public String getState() {
			return state;
		}

		public String getCallBack() {
			return callBack;
		}

		public String getStimatedDeliveryTime() {
			return stimatedDeliveryTime;
		}

		public List<Traces> getTraces() {
			return traces;
		}
	}
}
