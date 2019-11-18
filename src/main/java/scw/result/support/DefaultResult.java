package scw.result.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import scw.json.JSONUtils;
import scw.mvc.Text;
import scw.result.DataResult;

public class DefaultResult<T> extends DefaultBaseDataResult<T> implements DataResult<T>, Serializable, Text {
	private static final long serialVersionUID = 1L;
	private int code;
	private String contentType;

	public DefaultResult(boolean success, int code, T data, String msg, String contentType) {
		super(success, msg, data);
		this.code = code;
		this.contentType = contentType;
	}

	public int getCode() {
		return code;
	}

	public boolean isRollbackOnly() {
		return isError();
	}

	public boolean isError() {
		return !isSuccess();
	}

	public String getTextContent() {
		Map<String, Object> map = new HashMap<String, Object>(4, 1);
		map.put("success", isSuccess());
		map.put("code", getCode());
		map.put("data", getData());
		map.put("msg", getMsg());
		return JSONUtils.toJSONString(map);
	}

	public String getTextContentType() {
		return contentType;
	}
}
