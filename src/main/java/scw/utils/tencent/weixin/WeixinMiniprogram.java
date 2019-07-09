package scw.utils.tencent.weixin;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.json.JSONArray;
import scw.core.json.JSONObject;

/**
 * 微信小程序实现
 * 
 * @author shuchaowen
 *
 */
public class WeixinMiniprogram {
	public enum TemplateParameterName {
		/**
		 * target_state = 0 时必填，文字内容模板中 member_count 的值
		 */
		member_count,
		/**
		 * target_state = 0 时必填，文字内容模板中 room_limit 的值
		 */
		room_limit,
		/**
		 * target_state = 1
		 * 时必填，点击「进入」启动小程序时使用的路径。对于小游戏，没有页面的概念，可以用于传递查询字符串（query），如 "?foo=bar"
		 */
		path,
		/**
		 * target_state = 1
		 * 时必填，点击「进入」启动小程序时使用的版本。有效参数值为：develop（开发版），trial（体验版），release（正式版）
		 */
		version_type,;
	}

	/**
	 * 
	 * 状态 文字内容 颜色 允许转移的状态 0 "成员正在加入，当前 {member_count}/{room_limit} 人" #FA9D39 0,
	 * 1 1 "已开始" #CCCCCC 无
	 * 
	 * 活动的默认有效期是 24 小时。活动结束后，消息内容会变成统一的样式： 文字内容：“已结束” 文字颜色：#00ff00
	 * 
	 * @author shuchaowen
	 *
	 */
	public enum TargetState {
		NotYetBegun(0), AlreadyBegun(1),;

		private final int state;

		private TargetState(int state) {
			this.state = state;
		}

		public int getState() {
			return state;
		}
	}

	private final String appId;
	private final String appSecret;

	public WeixinMiniprogram(String appId, String appSecret) {
		this.appId = appId;
		this.appSecret = appSecret;
	}

	public final String getAppId() {
		return appId;
	}

	public final String getAppSecret() {
		return appSecret;
	}

	public Session code2session(String js_code) {
		StringBuilder sb = new StringBuilder("https://api.weixin.qq.com/sns/jscode2session");
		sb.append("?appid=").append(appId);
		sb.append("&secret=").append(appSecret);
		sb.append("&js_code=").append(js_code);
		sb.append("&grant_type=authorization_code");
		JSONObject json = WeiXinUtils.doGet(sb.toString());
		return new Session(json);
	}

	/**
	 * 组合模板并添加至帐号下的个人模板库
	 * 
	 * @param access_token
	 *            接口调用凭证
	 * @param id
	 *            模板标题id，可通过接口获取，也可登录小程序后台查看获取
	 * @param keyword_id_list
	 *            开发者自行组合好的模板关键词列表，关键词顺序可以自由搭配（例如[3,5,4]或[4,5,3]），最多支持10个关键词组合
	 */
	public AddTemplateResponse addTemplate(String access_token, String id, Collection<Integer> keyword_id_list) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("keyword_id_list", keyword_id_list);
		JSONObject json = WeiXinUtils
				.doPost("https://api.weixin.qq.com/cgi-bin/wxopen/template/add?access_token=" + access_token, map);
		return new AddTemplateResponse(json);
	}

	/**
	 * 创建被分享动态消息的 activity_id
	 * 
	 * @author shuchaowen
	 *
	 */
	public CreateActivityIdResponse createActivityId(String access_token) {
		JSONObject json = WeiXinUtils.doPost(
				"https://api.weixin.qq.com/cgi-bin/message/wxopen/activityid/create?access_token=" + access_token,
				null);
		return new CreateActivityIdResponse(json);
	}

	public BaseResponse deleteTemplate(String access_token, String template_id) {
		Map<String, String> map = new HashMap<String, String>(2, 1);
		map.put("template_id", template_id);
		JSONObject json = WeiXinUtils
				.doPost("https://api.weixin.qq.com/cgi-bin/wxopen/template/del?access_token=" + access_token, map);
		return new BaseResponse(json);
	}

	public GetTemplateLibraryByIdResponse getTemplateLibraryById(String access_token, String id) {
		Map<String, String> map = new HashMap<String, String>(2, 1);
		map.put("id", id);
		JSONObject json = WeiXinUtils.doPost(
				"https://api.weixin.qq.com/cgi-bin/wxopen/template/library/get?access_token=" + access_token, map);
		return new GetTemplateLibraryByIdResponse(json);
	}

	public GetTemplateLibraryListResponse getTemplateLibraryList(String access_token, int offset, int count) {
		Map<String, Object> map = new HashMap<String, Object>(2, 1);
		map.put("offset", offset);
		map.put("count", count);
		JSONObject json = WeiXinUtils.doPost(
				"https://api.weixin.qq.com/cgi-bin/wxopen/template/library/list?access_token=" + access_token, map);
		return new GetTemplateLibraryListResponse(json);
	}

	/**
	 * 
	 * @param access_token
	 * @param offset
	 *            用于分页，表示从offset开始。从 0 开始计数。
	 * @param count
	 *            用于分页，表示拉取count条记录。最大为 20。最后一页的list长度可能小于请求的count。
	 */
	public GetTemplateListResponse getTemplateList(String access_token, int offset, int count) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("offset", offset);
		map.put("count", count);
		JSONObject json = WeiXinUtils
				.doPost("https://api.weixin.qq.com/cgi-bin/wxopen/template/list?access_token=" + access_token, map);
		return new GetTemplateListResponse(json);
	}

	/**
	 * 发送模板消息
	 * 
	 * @param access_token
	 * @param touser
	 *            接收者（用户）的 openid
	 * @param weappTemplateMsg
	 *            属性 类型 必填 说明 template_id string 是 所需下发的模板消息的id page string 否
	 *            点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。
	 *            form_id string 是 表单提交场景下，为 submit 事件带上的 formId；支付场景下，为本次支付的
	 *            prepay_id data string 否 模板内容，不填则下发空模板 emphasis_keyword string
	 *            否 模板需要放大的关键词，不填则默认无放大
	 */
	public BaseResponse sendTemplateMessage(String access_token, String touser, WeappTemplateMsg weappTemplateMsg) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("touser", touser);
		map.put("template_id", weappTemplateMsg.getTemplate_id());
		map.put("page", weappTemplateMsg.getPage());
		map.put("form_id", weappTemplateMsg.getForm_id());
		map.put("data", weappTemplateMsg.getData());
		map.put("emphasis_keyword", weappTemplateMsg.getEmphasis_keyword());
		JSONObject json = WeiXinUtils.doPost(
				"https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=" + access_token, map);
		return new BaseResponse(json);
	}

	/**
	 * @param access_token
	 * @param activity_id
	 *            动态消息的 ID，通过 createActivityId 接口获取
	 * @param target_state
	 *            动态消息修改后的状态（具体含义见后文）
	 * @param parameter_list
	 *            动态消息对应的模板信息
	 */
	public BaseResponse setUpdatableMsg(String access_token, String activity_id, TargetState target_state,
			EnumMap<TemplateParameterName, String> parameter_list) {
		Map<String, Object> map = new HashMap<String, Object>(4, 1);
		map.put("activity_id", activity_id);
		map.put("target_state", target_state.getState());

		Map<String, Object> template_info = new HashMap<String, Object>(2);
		List<Object> list = new ArrayList<Object>();
		for (Entry<TemplateParameterName, String> entry : parameter_list.entrySet()) {
			Map<String, Object> json = new HashMap<String, Object>(2, 1);
			json.put("name", entry.getKey().name());
			json.put("value", entry.getValue());
			list.add(json);
		}
		template_info.put("parameter_list", list);
		map.put("template_info", template_info);
		JSONObject json = WeiXinUtils.doPost(
				"https://api.weixin.qq.com/cgi-bin/message/wxopen/updatablemsg/send?access_token=" + access_token, map);
		return new BaseResponse(json);
	}

	public static final class AddTemplateResponse extends BaseResponse {
		private static final long serialVersionUID = 1L;
		private String template_id;

		public AddTemplateResponse(JSONObject json) {
			super(json);
			if (isSuccess()) {
				this.template_id = json.getString("template_id");
			}
		}

		public String getTemplate_id() {
			return template_id;
		}
	}

	public static final class Session extends BaseResponse {
		private static final long serialVersionUID = 1L;
		private String openid;
		private String session_key;
		private String unionid;

		Session() {
			super(null);
		};

		public Session(JSONObject json) {
			super(json);
			this.openid = json.getString("openid");
			this.session_key = json.getString("session_key");
			this.unionid = json.getString("unionid");
		}

		public String getOpenid() {
			return openid;
		}

		public String getSession_key() {
			return session_key;
		}

		public String getUnionid() {
			return unionid;
		}
	}

	public static final class CreateActivityIdResponse extends BaseResponse {
		private static final long serialVersionUID = 1L;
		private String activity_id;
		private Long expiration_time;

		public CreateActivityIdResponse(JSONObject json) {
			super(json);
			if (isSuccess()) {
				this.activity_id = json.getString("activity_id");
				this.expiration_time = json.getLong("expiration_time");
			}
		}

		public String getActivity_id() {
			return activity_id;
		}

		public Long getExpiration_time() {
			return expiration_time;
		}
	}

	public static final class Keyword implements Serializable {
		private static final long serialVersionUID = 1L;
		private String keyword_id;// keyword_id
		private String name;// name
		private String example;// example

		Keyword() {
		};

		public Keyword(String keyword_id, String name, String example) {
			this.keyword_id = keyword_id;
			this.name = name;
			this.example = example;
		}

		public String getKeyword_id() {
			return keyword_id;
		}

		public String getName() {
			return name;
		}

		public String getExample() {
			return example;
		}
	}

	public static final class GetTemplateLibraryByIdResponse extends BaseResponse {
		private static final long serialVersionUID = 1L;
		private String id;// 模板标题 id
		private String title;// 模板标题
		private List<Keyword> keyword_list;// 关键词列表

		public GetTemplateLibraryByIdResponse(JSONObject json) {
			super(json);
			if (isSuccess()) {
				this.id = json.getString("id");
				this.title = json.getString("title");
				JSONArray jsonArray = json.getJSONArray("keyword_list");
				if (jsonArray != null) {
					this.keyword_list = new ArrayList<Keyword>();
					for (int i = 0; i < jsonArray.size(); i++) {
						keyword_list.add(jsonArray.getObject(i, Keyword.class));
					}
				}
			}
		}

		public String getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public List<Keyword> getKeyword_list() {
			return keyword_list;
		}
	}

	public static final class TemplateLibrary implements Serializable {
		private static final long serialVersionUID = 1L;
		private String id;// 模板标题id（获取模板标题下的关键词库时需要）
		private String title;// 模板标题内容

		public TemplateLibrary() {
		};

		public TemplateLibrary(String id, String title) {
			this.id = id;
			this.title = title;
		}

		public String getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}
	}

	public static final class GetTemplateLibraryListResponse extends BaseResponse {
		private static final long serialVersionUID = 1L;
		private List<TemplateLibrary> list;
		private int total_count;// 模板库标题总数

		GetTemplateLibraryListResponse() {
			super(null);
		}

		GetTemplateLibraryListResponse(JSONObject json) {
			super(json);
			if (isSuccess()) {
				this.total_count = json.getIntValue("total_count");
				JSONArray jsonArray = json.getJSONArray("list");
				if (jsonArray != null) {
					this.list = new ArrayList<TemplateLibrary>();
					for (int i = 0; i < jsonArray.size(); i++) {
						list.add(jsonArray.getObject(i, TemplateLibrary.class));
					}
				}
			}
		}

		public List<TemplateLibrary> getList() {
			return list;
		}

		public int getTotal_count() {
			return total_count;
		}
	}

	public static final class Template implements Serializable {
		private static final long serialVersionUID = 1L;
		private String template_id;// 添加至帐号下的模板id，发送小程序模板消息时所需
		private String title;// 模板标题
		private String content;// 模板内容
		private String example;// 模板内容示例

		public String getTemplate_id() {
			return template_id;
		}

		public void setTemplate_id(String template_id) {
			this.template_id = template_id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getExample() {
			return example;
		}

		public void setExample(String example) {
			this.example = example;
		}
	}

	public static final class GetTemplateListResponse extends BaseResponse {
		private static final long serialVersionUID = 1L;
		private List<Template> list;

		GetTemplateListResponse() {
			super(null);
		}

		public GetTemplateListResponse(JSONObject json) {
			super(json);
			if (isSuccess()) {
				JSONArray jsonArray = json.getJSONArray("list");
				if (jsonArray != null) {
					this.list = new ArrayList<Template>();
					for (int i = 0; i < jsonArray.size(); i++) {
						list.add(jsonArray.getObject(i, Template.class));
					}
				}
			}
		}

		public List<Template> getList() {
			return list;
		}
	}

	/**
	 * 小程序模板消息相关的信息
	 * 
	 * @author shuchaowen
	 *
	 */
	public static final class WeappTemplateMsg implements Serializable {
		private static final long serialVersionUID = 1L;
		private String template_id;// 小程序模板ID
		private String page;// 点击模板卡片后的跳转页面，仅限本小程序内的页面。支持带参数,（示例index?foo=bar）。该字段不填则模板无跳转。
		private String form_id;// 小程序模板消息formid
		private String data;// 小程序模板数据
		private String emphasis_keyword;// 小程序模板放大关键词

		public WeappTemplateMsg() {
		};

		public WeappTemplateMsg(String template_id, String page, String form_id, String data, String emphasis_keyword) {
			this.template_id = template_id;
			this.page = page;
			this.form_id = form_id;
			this.data = data;
			this.emphasis_keyword = emphasis_keyword;
		}

		public String getTemplate_id() {
			return template_id;
		}

		public void setTemplate_id(String template_id) {
			this.template_id = template_id;
		}

		public String getPage() {
			return page;
		}

		public void setPage(String page) {
			this.page = page;
		}

		public String getForm_id() {
			return form_id;
		}

		public void setForm_id(String form_id) {
			this.form_id = form_id;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getEmphasis_keyword() {
			return emphasis_keyword;
		}

		public void setEmphasis_keyword(String emphasis_keyword) {
			this.emphasis_keyword = emphasis_keyword;
		}
	}
}
