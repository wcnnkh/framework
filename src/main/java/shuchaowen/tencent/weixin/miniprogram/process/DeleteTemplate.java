package shuchaowen.tencent.weixin.miniprogram.process;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.tencent.weixin.WeiXinProcess;

public final class DeleteTemplate extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/wxopen/template/del?access_token=";
	
	public DeleteTemplate(String access_token, String template_id){
		Map<String, String> map = new HashMap<String, String>();
		map.put("template_id", template_id);
		post(API + access_token, map);
	}
}
