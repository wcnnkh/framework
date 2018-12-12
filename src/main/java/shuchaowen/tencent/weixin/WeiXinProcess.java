package shuchaowen.tencent.weixin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.common.utils.StringUtils;
import shuchaowen.connection.http.HttpUtils;

public abstract class WeiXinProcess{
	private static final Map<String, String> postRequestProperties = new HashMap<String, String>(2);
	static{
		postRequestProperties.put("Content-type", "application/json;charset=utf-8");
	}
	
	private int errcode;//错误码
	private String errmsg;//错误信息
	
	public int getErrcode() {
		return errcode;
	}
	
	public String getErrmsg() {
		return errmsg;
	}
	
	public boolean isSuccess(){
		return errcode == 0;
	}
	
	protected JSONObject post(String url, Object data){
		String body = null;
		if(data != null){
			body = JSONObject.toJSONString(body);
		}
		
		String content =  HttpUtils.doPost(url, postRequestProperties, body);
		if(StringUtils.isNull(content)){
			throw new ShuChaoWenRuntimeException("请求错误：url=" + url + ", body=" + body);
		}
		
		JSONObject json = JSONObject.parseObject(content);
		this.errcode = json.getIntValue("errcode");
		this.errmsg = json.getString("errmsg");
		return json;
	}
	
	protected JSONObject get(String url){
		String content =  HttpUtils.doGet(url);
		if(StringUtils.isNull(content)){
			throw new ShuChaoWenRuntimeException("请求错误:" + url);
		}
		
		JSONObject json = JSONObject.parseObject(content);
		this.errcode = json.getIntValue("errcode");
		this.errmsg = json.getString("errmsg");
		return json;
	}
}
