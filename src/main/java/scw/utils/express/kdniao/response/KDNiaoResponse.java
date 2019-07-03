package scw.utils.express.kdniao.response;

import java.io.Serializable;

import scw.json.JSONObject;

/**
 * 快递鸟通用返回
 * 
 * @author asus1
 *
 */
public class KDNiaoResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	// 成功与否
	private boolean success;
	// 失败原因
	private String reason;

	public KDNiaoResponse(JSONObject json) {
		this.success = json.getBooleanValue("Success");
		this.reason = json.getString("Reason");
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
