package scw.utils.tencent.weixin;

import java.io.Serializable;

import scw.core.json.JSONObject;

public class BaseResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private int errcode;// 错误码
	private String errmsg;// 错误信息

	BaseResponse() {
	}

	public BaseResponse(JSONObject json) {
		if (json == null) {
			return;
		}

		this.errcode = json.getIntValue("errcode");
		this.errmsg = json.getString("errmsg");
	}

	public final int getErrcode() {
		return errcode;
	}

	public final String getErrmsg() {
		return errmsg;
	}

	public boolean isSuccess() {
		return errcode == 0;
	}

	public boolean isError() {
		return errcode != 0;
	}
}
