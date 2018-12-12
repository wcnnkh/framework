package shuchaowen.tencent.weixin.miniprogram.process;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.tencent.weixin.WeiXinProcess;
import shuchaowen.tencent.weixin.miniprogram.bean.WeappTemplateMsg;

/**
 * 发送模板消息
 * @author shuchaowen
 *
 */
public final class SendTemplateMessage extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=";
	
	/**
	 * 发送模板消息
	 * @param access_token
	 * @param touser 接收者（用户）的 openid
	 * @param weappTemplateMsg 
	 */
	public SendTemplateMessage(String access_token, String touser, WeappTemplateMsg weappTemplateMsg){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("touser", touser);
		map.put("template_id", weappTemplateMsg.getTemplate_id());
		map.put("page", weappTemplateMsg.getPage());
		map.put("form_id", weappTemplateMsg.getForm_id());
		map.put("data", weappTemplateMsg.getData());
		map.put("emphasis_keyword", weappTemplateMsg.getEmphasis_keyword());
		post(API + access_token, map);
	}
}
