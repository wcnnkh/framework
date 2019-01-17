package scw.utils.tencent.weixin.process;

import com.alibaba.fastjson.JSONObject;

import scw.utils.tencent.weixin.WeiXinProcess;
import scw.utils.tencent.weixin.bean.JsApiTicket;

public final class GetJsApiTicket extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
	
	private JsApiTicket ticket;
	
	public GetJsApiTicket(String access_token){
		this(access_token, "jsapi");
	}
	
	public GetJsApiTicket(String access_token, String type){
		StringBuilder sb = new StringBuilder(API);
		sb.append("?access_token=").append(access_token);
		sb.append("&type=").append(type);
		JSONObject json = get(sb.toString());
		if(isSuccess()){
			this.ticket = new JsApiTicket(json.getString("ticket"), json.getIntValue("expires_in"));
		}
	}

	public JsApiTicket getTicket() {
		return ticket;
	}
}
