package scw.tencent.wx.pay;

import java.util.Map;

public class UnifiedOrderResponse extends PayBaseResponse {
	private static final long serialVersionUID = 1L;

	public UnifiedOrderResponse(Map<String, String> responseMap) {
		super(responseMap);
	}

	@Override
	public boolean isSuccess() {
		return super.isSuccess() && isResultCodeSuccess();
	}

	public boolean isResultCodeSuccess() {
		return SUCCESS.equals(getResultCode());
	}

	public String getAppId() {
		return get("appid");
	}

	public String getMchId() {
		return get("mch_id");
	}

	public String getDeviceInfo() {
		return get("device_info");
	}

	public String getNonceStr() {
		return get("nonce_str");
	}

	public String getSign() {
		return get("sign");
	}

	public String getResultCode() {
		return get("result_code");
	}

	public String getErrCode() {
		return get("err_code");
	}

	public String getErrCodeDes() {
		return get("err_code_des");
	}

	public String getTradeType() {
		return get("trade_type");
	}

	public String getPrepayId() {
		return get("prepay_id");
	}

	public String getCodeUrl() {
		return get("code_url");
	}
}
