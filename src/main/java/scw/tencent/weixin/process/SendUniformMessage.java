package scw.tencent.weixin.process;

import java.util.HashMap;
import java.util.Map;

import scw.tencent.weixin.WeiXinProcess;
import scw.tencent.weixin.bean.MpTemplateMsg;
import scw.tencent.weixin.miniprogram.bean.WeappTemplateMsg;

public final class SendUniformMessage extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/uniform_send?access_token=";

	public SendUniformMessage(String access_token, String touser, WeappTemplateMsg weapp_template_msg, MpTemplateMsg mp_template_msg){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("touser", touser);
		map.put("weapp_template_msg", weapp_template_msg);
		map.put("mp_template_msg", mp_template_msg);
		post(API + access_token, map);
	}
}
