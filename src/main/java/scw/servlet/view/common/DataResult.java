package scw.servlet.view.common;

import com.alibaba.fastjson.JSONObject;

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
		JSONObject json = new JSONObject(4);
		json.put("code", getCode());
		json.put("msg", getMsg());
		json.put("data", getData());
		return json.toJSONString();
	}
}
