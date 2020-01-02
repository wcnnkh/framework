package scw.result.support;

import java.util.HashMap;
import java.util.Map;

import scw.json.JSONUtils;
import scw.result.DataResult;

public class DefaultDataResult<T> extends DefaultResult implements DataResult<T> {
	private static final long serialVersionUID = 1L;
	private T data;

	public DefaultDataResult(boolean success, int code, T data, String msg, String contentType, boolean rollbackOnly) {
		super(success, code, msg, rollbackOnly, contentType);
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public String getTextContent() {
		Map<String, Object> map = new HashMap<String, Object>(4, 1);
		map.put("success", isSuccess());
		map.put("code", getCode());
		map.put("data", getData());
		map.put("msg", getMsg());
		return JSONUtils.toJSONString(map);
	}
}
