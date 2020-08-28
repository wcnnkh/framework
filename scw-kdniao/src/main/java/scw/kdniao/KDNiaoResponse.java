package scw.kdniao;

import java.io.Serializable;

import scw.json.JsonObject;

/**
 * 快递鸟通用返回
 * 
 * @author shuchaowen
 *
 */
public class KDNiaoResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	// 用户ID
	private String businessId;
	// 成功与否
	private boolean success;
	// 失败原因
	private String reason;

	public KDNiaoResponse(JsonObject json) {
		if (json != null) {
			this.success = json.getBooleanValue("Success");
			this.reason = json.getString("Reason");
			this.businessId = json.getString("EBusinessID");
		}
	}

	/**
	 * 用户ID
	 * 
	 * @return
	 */
	public final String getBusinessId() {
		return businessId;
	}

	/**
	 * 成功与否
	 * 
	 * @return
	 */
	public final boolean isSuccess() {
		return success;
	}

	/**
	 * 失败原因
	 * 
	 * @return
	 */
	public final String getReason() {
		return reason;
	}
}
