package scw.utils.tencent.weixin.miniprogram.process;

import java.util.HashMap;
import java.util.Map;

import scw.utils.tencent.weixin.WeiXinProcess;
import scw.utils.tencent.weixin.miniprogram.bean.WeappTemplateMsg;

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
	 *  属性					类型			必填	说明
	 *  template_id			string		是	所需下发的模板消息的id	
		page	    		string		否	点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。	
		form_id	    		string		是	表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的 prepay_id	
		data	    		string		否	模板内容，不填则下发空模板	
		emphasis_keyword	string		否	模板需要放大的关键词，不填则默认无放大
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
