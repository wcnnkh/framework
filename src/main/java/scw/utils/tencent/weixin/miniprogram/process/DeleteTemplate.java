package scw.utils.tencent.weixin.miniprogram.process;

import java.util.HashMap;
import java.util.Map;

import scw.utils.tencent.weixin.WeiXinProcess;

/**
 * 删除帐号下的某个模板
 * @author shuchaowen
 *
 */
public final class DeleteTemplate extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/wxopen/template/del?access_token=";
	
	/**
	 * @param access_token
	 * @param template_id 要删除的模板id
	 */
	public DeleteTemplate(String access_token, String template_id){
		Map<String, String> map = new HashMap<String, String>();
		map.put("template_id", template_id);
		post(API + access_token, map);
	}
}
