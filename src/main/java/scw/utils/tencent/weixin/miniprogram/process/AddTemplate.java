package scw.utils.tencent.weixin.miniprogram.process;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import scw.json.JSONObject;
import scw.utils.tencent.weixin.WeiXinProcess;

/**
 * 组合模板并添加至帐号下的个人模板库
 * @author shuchaowen
 *
 */
public final class AddTemplate extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/wxopen/template/add?access_token=";
	
	private String template_id;
	
	/**
	 * 组合模板并添加至帐号下的个人模板库
	 * @param access_token 接口调用凭证
	 * @param id 模板标题id，可通过接口获取，也可登录小程序后台查看获取
	 * @param keyword_id_list 开发者自行组合好的模板关键词列表，关键词顺序可以自由搭配（例如[3,5,4]或[4,5,3]），最多支持10个关键词组合
	 */
	public AddTemplate(String access_token, String id, Collection<Integer> keyword_id_list){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("keyword_id_list", keyword_id_list);
		JSONObject json = post(API + access_token, map);
		if(isSuccess()){
			this.template_id = json.getString("template_id");
		}
	}

	public String getTemplate_id() {
		return template_id;
	}
}
