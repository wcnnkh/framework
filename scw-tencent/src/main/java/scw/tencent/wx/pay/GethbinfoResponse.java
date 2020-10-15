package scw.tencent.wx.pay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.json.JsonObjectWrapper;

public class GethbinfoResponse extends WeiXinPayResponse {

	public GethbinfoResponse(JsonObject target) {
		super(target);
	}

	/**
	 * 商户使用查询API填写的商户单号的原路返回
	 * 
	 * @return
	 */
	public String getMchBillno() {
		return getString("mch_billno");
	}

	/**
	 * 使用API发放现金红包时返回的红包单号
	 * 
	 * @return
	 */
	public String getDetailId() {
		return getString("detail_id");
	}

	public HbStatus getStatus() {
		return (HbStatus) getEnum("status", HbStatus.class);
	}

	public SendType getSendType() {
		return (SendType) getEnum("send_type", SendType.class);
	}

	public HbType getHbType() {
		return (HbType) getEnum("hb_type", HbType.class);
	}

	public int getTotalNum() {
		return getIntValue("total_num");
	}

	/**
	 * 红包总金额（单位分）
	 * 
	 * @return
	 */
	public int getTotalAmount() {
		return getIntValue("total_amount");
	}

	/**
	 * 发送失败原因
	 * 
	 * @return
	 */
	public String getReason() {
		return getString("reason");
	}

	/**
	 * 红包发送时间 2015-04-21 20:00:00
	 * 
	 * @return
	 */
	public String getSendTime() {
		return getString("send_time");
	}

	/**
	 * 红包的退款时间（如果其未领取的退款）
	 * 
	 * @return
	 */
	public String getRefundTime() {
		return getString("refund_time");
	}

	/**
	 * 红包退款金额
	 * 
	 * @return
	 */
	public int getRefundAmount() {
		return getIntValue("refund_amount");
	}

	public String getWishing() {
		return getString("wishing");
	}

	public String getremark() {
		return getString("remark");
	}

	/**
	 * 活动名称
	 * 
	 * @return
	 */
	public String getActName() {
		return getString("act_name");
	}

	/**
	 * 裂变红包的领取列表
	 * 
	 * @return
	 */
	public List<HbInfo> getHbList() {
		JsonArray jsonArray = getJsonArray("hblist");
		if (jsonArray == null) {
			return null;
		}

		if (jsonArray.isEmpty()) {
			return Collections.emptyList();
		}

		List<HbInfo> list = new ArrayList<HbInfo>();
		for (int i = 0, len = jsonArray.size(); i < len; i++) {
			list.add(new HbInfo(jsonArray.getJsonObject(i)));
		}
		return list;
	}

	public static class HbInfo extends JsonObjectWrapper {

		public HbInfo(JsonObject target) {
			super(target);
		}

		public String getOpenid() {
			return getString("openid");
		}

		public int getAmount() {
			return getIntValue("amount");
		}

		/**
		 * 领取红包的时间 2015-04-21 20:00:00
		 * 
		 * @return
		 */
		public String getRcvTime() {
			return getString("rcv_time");
		}
	}
}
