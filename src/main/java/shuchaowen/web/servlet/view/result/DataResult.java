package shuchaowen.web.servlet.view.result;

import com.alibaba.fastjson.JSONObject;

public class DataResult<T> extends Result{
	private static final long serialVersionUID = 1L;
	private T data;
	
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
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
