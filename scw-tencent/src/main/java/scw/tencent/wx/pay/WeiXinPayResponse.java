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

	/**
	 * 应用APPID
	 * @return
	 */
	public String getAppId() {
		return getString("appid");
	}

	/**
	 * 商户号
	 * @return
	 */
	public String getMchId() {
		return getString("mch_id");
	}

	/**
	 * 微信支付分配的终端设备号，
	 * @return
	 */
	public String getDeviceInfo() {
		return getString("device_info");
	}

	/**
	 * 随机字符串
	 * @return
	 */
	public String getNonceStr() {
		return getString("nonce_str");
	}

	/**
	 * 签名
	 * @return
	 */
	public String getSign() {
		return getString("sign");
	}
}
