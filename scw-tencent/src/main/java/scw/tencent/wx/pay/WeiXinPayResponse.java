package scw.tencent.wx.pay;

import scw.json.JsonObject;
import scw.json.JsonObjectWrapper;

public class WeiXinPayResponse extends JsonObjectWrapper {
	private static final String SUCCESS_TEXT = "SUCCESS";

	public WeiXinPayResponse(JsonObject target) {
		super(target);
	}

	public boolean isReturnSuccess() {
		return containsKey("return_code") && SUCCESS_TEXT.equals(getString("return_code"));
	}

	public String getReturnMsg() {
		return getString("return_msg");
	}

	public boolean isResultSuccess() {
		return containsKey("result_code") && SUCCESS_TEXT.equals(getString("result_code"));
	}

	public String getResultErrCodeDes() {
		return getString("err_code_des");
	}

	public String getResultErrCode() {
		return getString("err_code");
	}

	public boolean isSuccess() {
		return isReturnSuccess() && isResultSuccess();
	}

	public String getAppId() {
		return getString("appid");
	}

	public String getMchId() {
		return getString("mch_id");
	}

	public String getDeviceInfo() {
		return getString("device_info");
	}

	public String getNonceStr() {
		return getString("nonce_str");
	}

	public String getSign() {
		return getString("sign");
	}
}
