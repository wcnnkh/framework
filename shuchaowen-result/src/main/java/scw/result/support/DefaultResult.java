package scw.result.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import scw.json.JSONUtils;
import scw.net.Text;
import scw.net.mime.MimeType;
import scw.result.Result;
import scw.transaction.RollbackOnlyResult;

public class DefaultResult implements Result, RollbackOnlyResult, Text, Serializable {
	private static final long serialVersionUID = 1L;
	private boolean success;
	private int code;
	private String msg;
	private boolean rollbackOnly;
	private MimeType mimeType;

	public DefaultResult(boolean success, int code, String msg, boolean rollbackOnly, MimeType mimeType) {
		this.success = success;
		this.code = code;
		this.msg = msg;
		this.rollbackOnly = rollbackOnly;
		this.mimeType = mimeType;
	}

	public boolean isError() {
		return !isSuccess();
	}

	public int getCode() {
		return code;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getMsg() {
		return msg;
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	public String getTextContent() {
		Map<String, Object> map = new HashMap<String, Object>(4, 1);
		map.put("success", isSuccess());
		map.put("code", getCode());
		map.put("msg", getMsg());
		return JSONUtils.toJSONString(map);
	}

	public MimeType getMimeType() {
		return mimeType;
	}
}
