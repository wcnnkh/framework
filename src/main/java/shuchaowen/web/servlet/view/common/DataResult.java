package shuchaowen.web.servlet.view.common;

import com.alibaba.fastjson.JSONObject;

public class DataResult<T> extends Result{
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
	public String getResponseText() {
		JSONObject json = new JSONObject(4);
		json.put("code", getCode());
		json.put("msg", getMsg());
		json.put("data", getData());
		return json.toJSONString();	
	}
}
