package scw.servlet.view.common;

import java.util.HashMap;
import java.util.Map;

import scw.json.JSONUtils;
import scw.servlet.view.common.enums.ResultCode;

public class DataResult<T> extends Result {
	private static final long serialVersionUID = 1L;
	private T data;

	public T getData() {
		return data;
	}

	public DataResult<T> setData(T data) {
		this.data = data;
		return this;
	}

	@Override
	public DataResult<T> setCode(ResultCode code) {
		super.setCode(code);
		return this;
	}

	@Override
	public DataResult<T> setCode(int code, String msg) {
		super.setCode(code, msg);
		return this;
	}

	@Override
	public DataResult<T> setMsg(String msg) {
		super.setMsg(msg);
		return this;
	};

	@Override
	public String getResponseText() {
		Map<String, Object> map = new HashMap<String, Object>(3, 1);
		map.put("code", getCode());
		map.put("msg", getMsg());
		map.put("data", getData());
		return JSONUtils.toJSONString(map);
	}
}
