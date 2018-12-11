package shuchaowen.tencent.weixin.miniprogram;

import java.util.HashMap;
import java.util.Map;

import shuchaowen.connection.http.HttpUtils;

/**
 * 小程序工具类
 * @author asus1
 *
 */
public final class MiniProgramUtils {
	private static final String TEMPLATE_ADD_URL = "https://api.weixin.qq.com/cgi-bin/wxopen/template/add?access_token=";
	private static final String TEMPLATE_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/wxopen/template/del?access_token=";
	private static final String TEMPLATE_LIBRARY_URL = "https://api.weixin.qq.com/cgi-bin/wxopen/template/library/get?access_token=";
	private static final String TEMPLATE_LIBRARY_LIST_URL = "https://api.weixin.qq.com/cgi-bin/wxopen/template/library/list?access_token=";
	private static final String TEMPLATE_LIST_URL = "https://api.weixin.qq.com/cgi-bin/wxopen/template/list?access_token=";
	private static final String TEMPLATE_SEND_URL = "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=";
	
	private MiniProgramUtils(){};
	
	public static String addTemplate(String access_token, String id, String keyword_id_list){
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("keyword_id_list", keyword_id_list);
		return HttpUtils.doPost(TEMPLATE_ADD_URL + access_token, map);
	}
	
	public static String deleteTemplate(String access_token, String template_id){
		Map<String, String> map = new HashMap<String, String>();
		map.put("template_id", template_id);
		return HttpUtils.doPost(TEMPLATE_DELETE_URL + access_token, map);
	}
	
	public static String getTemplateLibraryById(String access_token, String id){
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		return HttpUtils.doPost(TEMPLATE_LIBRARY_URL + access_token, map);
	}
	
	public static String getTemplateLibraryList(String access_token, int offset, int count){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", offset);
		map.put("count", count);
		return HttpUtils.doPost(TEMPLATE_LIBRARY_LIST_URL + access_token, map);
	}
	
	public static String getTemplateList(String access_token, int offset, int count){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", offset);
		map.put("count", count);
		return HttpUtils.doPost(TEMPLATE_LIST_URL + access_token, map);
	}
	
	public static String sendTemplateMessage(String access_token, String touser, String template_id,
			String page, String form_id, String data, String emphasis_keyword){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("touser", touser);
		map.put("template_id", template_id);
		map.put("page", page);
		map.put("form_id", form_id);
		map.put("data", data);
		map.put("emphasis_keyword", emphasis_keyword);
		return HttpUtils.doPost(TEMPLATE_SEND_URL + access_token, map);
	}
}
