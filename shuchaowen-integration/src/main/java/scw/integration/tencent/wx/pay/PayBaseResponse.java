package scw.integration.tencent.wx.pay;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class PayBaseResponse extends LinkedHashMap<String, String> implements Serializable {
	public static final String SUCCESS = "SUCCESS";
	public static final String FAIL = "FAIL";
	private static final long serialVersionUID = 1L;

	public PayBaseResponse(Map<String, String> responseMap) {
		super(responseMap);
	}

	public String getReturnCode() {
		return get("return_code");
	}

	public String getReturnMsg() {
		return get("return_msg");
	}

	public boolean isReturnCodeSuccess() {
		return SUCCESS.equals(getReturnCode());
	}

	public boolean isSuccess() {
		return isReturnCodeSuccess();
	}
}
