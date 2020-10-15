package scw.tencent.wx.miniprogram;

import scw.json.JsonObject;
import scw.json.JsonObjectWrapper;

public class BaseResponse extends JsonObjectWrapper {
	public BaseResponse(JsonObject target) {
		super(target);
	}

	public int getErrcode() {
		return getIntValue("errcode");
	}

	public String getErrmsg() {
		return getString("errmsg");
	}

	public boolean isSuccess() {
		return getErrcode() == 0;
	}

	public boolean isError() {
		return !isSuccess();
	}
}
