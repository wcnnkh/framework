package shuchaowen.tencent.weixin.bean;

import java.io.Serializable;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.common.exception.ShuChaoWenRuntimeException;
import shuchaowen.connection.http.HttpUtils;
import shuchaowen.core.util.StringUtils;
import shuchaowen.tencent.weixin.WeiXinUtils;

public class JsApiTicket implements Serializable {
	private static final String weixin_get_web_ticket = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

	private static final long serialVersionUID = 1L;
	private String ticket;
	private int expires_in;
	private long cts;// 创建时间

	public JsApiTicket() {
		this.cts = System.currentTimeMillis();
	}

	public JsApiTicket(String access_token) {
		load(access_token);
	}
	
	public void load(String access_token){
		String content = HttpUtils.doPost(weixin_get_web_ticket + "?access_token=" + access_token + "&type=jsapi", null);
		if (StringUtils.isNull(content)) {
			throw new ShuChaoWenRuntimeException("无法从微信服务器获取ticket");
		}

		JSONObject jsonObject = JSONObject.parseObject(content);
		if (WeiXinUtils.isError(jsonObject)) {
			throw new ShuChaoWenRuntimeException(content);
		}

		// 成功
		this.cts = System.currentTimeMillis();
		this.ticket = jsonObject.getString("ticket");
		this.expires_in = jsonObject.getIntValue("expires_in");
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	public long getCts() {
		return cts;
	}

	public void setCts(long cts) {
		this.cts = cts;
	}

	// 判断是否已经过期 提前5分钟过期
	public boolean isExpires() {
		return (System.currentTimeMillis() - cts) > (expires_in - 300) * 1000L;
	}
}
