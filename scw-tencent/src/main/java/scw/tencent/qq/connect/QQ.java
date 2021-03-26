package scw.tencent.qq.connect;

import java.util.HashMap;
import java.util.Map;

import scw.codec.support.URLCodec;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpResponseEntity;
import scw.http.HttpUtils;
import scw.http.MediaType;
import scw.json.JSONUtils;
import scw.json.JsonObject;
import scw.lang.Nullable;
import scw.net.uri.UriUtils;
import scw.oauth2.AccessToken;
import scw.security.Token;

/**
 * 根据qq互联文档实现
 * 
 * {@link https://wiki.connect.qq.com/}
 * 
 * @author shuchaowen
 *
 */
public class QQ {
	private static final String callbackPrefix = "callback( ";
	private static final String GET_USER_INFO = "https://graph.qq.com/user/get_user_info";
	private static final String GET_VIP_INFO = "https://graph.qq.com/user/get_vip_info";
	private static final String GET_VIP_RICH_INFO = "https://graph.qq.com/user/get_vip_rich_info";
	private static final String LIST_ALBUM = "https://graph.qq.com/photo/list_album";
	private static final String UPLOAD_PIC = "https://graph.qq.com/photo/upload_pic";
	private static final String ADD_ALBUM = "https://graph.qq.com/photo/add_album";
	private static final String LIST_PHOTO = "https://graph.qq.com/photo/list_photo";
	private static final String TOKEN = "https://graph.qq.com/oauth2.0/token";
	private static final String OAUTH2_ME = "https://graph.qq.com/oauth2.0/me";
	private static final String OAUTH2_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";

	private final String appId;
	private final String appKey;

	public QQ(String appId, String appKey) {
		this.appId = appId;
		this.appKey = appKey;
	}

	public final String getAppId() {
		return appId;
	}

	public final String getAppKey() {
		return appKey;
	}

	public JsonObject doGet(String url, QQRequest request, Map<String, ?> params) {
		StringBuilder sb = new StringBuilder(url);
		if (request != null) {
			sb.append("?access_token=").append(request.getAccessToken());
			sb.append("&oauth_consumer_key=").append(appId);
			sb.append("&openid=").append(request.getOpenid());
		}
		return response(HttpUtils.getHttpClient().get(String.class, UriUtils.appendQueryParams(sb.toString(), params, URLCodec.UTF_8)));
	}

	public JsonObject doPost(String url, QQRequest request, Map<String, ?> params, MediaType mediaType) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (request != null) {
			map.put("access_token", request.getAccessToken());
			map.put("oauth_consumer_key", appId);
			map.put("openid", request.getOpenid());
		}

		if (!CollectionUtils.isEmpty(params)) {
			map.putAll(params);
		}
		HttpResponseEntity<String> response = HttpUtils.getHttpClient().post(String.class, url, map, mediaType);
		return response(response);
	}

	public JsonObject response(HttpResponseEntity<String> response) {
		String content = response.getBody();
		if (content.startsWith(callbackPrefix)) {
			content = content.substring(callbackPrefix.length(), content.length() - 2);
		}

		return JSONUtils.getJsonSupport().parseObject(content);
	}

	public AccessToken getAccessToken(String redirect_uri, String code) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("grant_type", "authorization_code");
		map.put("client_id", appId);
		map.put("client_secret", appKey);
		map.put("redirect_uri", redirect_uri);
		map.put("code", code);
		String content = HttpUtils.getHttpClient().post(String.class, TOKEN, map, MediaType.APPLICATION_FORM_URLENCODED)
				.getBody();
		JsonObject json = JSONUtils.getJsonSupport().parseObject(content);
		if (json.getIntValue("code") != 0) {
			throw new RuntimeException(
					"url=" + TOKEN + ", data=" + JSONUtils.getJsonSupport().toJSONString(map) + ", response=" + content);
		}
		return new AccessToken(new Token(json.getString("access_token"), json.getIntValue("expires_in")), null,
				new Token(json.getString("refresh_token"), 0), null, null);
	}

	public String getOpenid(String access_token) {
		Map<String, Object> params = new HashMap<String, Object>(4);
		params.put("access_token", access_token);
		JsonObject response = doGet(OAUTH2_ME, null, params);
		return response.getString("openid");
	}

	/**
	 * 获取登录授权地址
	 * 
	 * @param redirect_uri
	 *            登录成功后的回调地址, 必须是注册appid时填写的主域名下的地址，建议设置为网站首页或网站的用户中心
	 * @param state
	 *            client端的状态值。用于第三方应用防止CSRF攻击，成功授权后回调时会原样带回。请务必严格按照流程检查用户与state参数状态的绑定。
	 * @param scope
	 *            请求用户授权时向用户显示的可进行授权的列表。
	 *            可填写的值是API文档中列出的接口，以及一些动作型的授权（目前仅有：do_like），如果要填写多个接口名称，请用逗号隔开。
	 *            例如：scope=get_user_info,list_album,upload_pic,do_like
	 *            不传则默认请求对接口get_user_info进行授权。
	 *            建议控制授权项的数量，只传入必要的接口名称，因为授权项越多，用户越可能拒绝进行任何授权。
	 * @param display
	 *            仅PC网站接入时使用。 用于展示的样式。不传则默认展示为PC下的样式。
	 *            如果传入“mobile”，则展示为mobile端下的样式。
	 * @return
	 */
	public String getAuthorizeUrl(String redirect_uri, String state, @Nullable String scope,
			@Nullable DisplayType display) {
		StringBuilder sb = new StringBuilder(OAUTH2_AUTHORIZE);
		sb.append("?");
		sb.append("response_type=code");
		sb.append("&client_id=").append(appId);
		sb.append("&redirect_uri=").append(UriUtils.encode(redirect_uri));
		sb.append("&state=" + state);
		if (StringUtils.isNotEmpty(scope)) {
			sb.append("&scope=" + scope);
		}

		if (display != null && display != DisplayType.PC) {
			sb.append(display.name().toLowerCase());
		}
		return sb.toString();
	}

	/**
	 * 此接口主要用于网站使用QQ登录时，直接拉取用户在QQ空间的昵称、头像、性别等信息，降低用户的注册成本。
	 * {@link https://wiki.connect.qq.com/get_user_info}
	 * 
	 * @param request
	 * @return
	 */
	public UserInfoResponse getUserinfo(QQRequest request) {
		JsonObject response = doGet(GET_USER_INFO, request, null);
		return new UserInfoResponse(response);
	}

	/**
	 * 获取已登录用户的关于QQ会员业务的基本资料。<br/>
	 * 基本资料包括以下信息：是否为“普通包月会员”，是否为“年费会员”，QQ会员等级信息，是否为“豪华版QQ会员”，是否为“钻皇会员”，是否为“SVIP”。
	 * {@link https://wiki.connect.qq.com/get_vip_info}
	 * 
	 * @param request
	 * @return
	 */
	public VipInfoResponse getVipInfo(QQRequest request) {
		JsonObject response = doGet(GET_VIP_INFO, request, null);
		return new VipInfoResponse(response);
	}

	/**
	 * 获取已登录用户的关于QQ会员业务的详细资料。<br/>
	 * 详细资料包括：用户会员的历史属性，用户会员特权的到期时间，用户最后一次充值会员业务的支付渠道，用户开通会员的主要驱动因素。
	 * {@link https://wiki.connect.qq.com/get_vip_rich_info}
	 * 
	 * @param request
	 * @return
	 */
	public VipRichInfoResponse getVipRichInfo(QQRequest request) {
		JsonObject response = doGet(GET_VIP_RICH_INFO, request, null);
		return new VipRichInfoResponse(response);
	}

	/**
	 * 获取登录用户的相册列表。 {@link https://wiki.connect.qq.com/list_album}
	 * 
	 * {@link https://wiki.connect.qq.com/list_album}
	 * 
	 * @param request
	 * @return
	 */
	public ListAlbumResponse listAlbum(QQRequest request) {
		JsonObject response = doGet(LIST_ALBUM, request, null);
		return new ListAlbumResponse(response);
	}

	/**
	 * 登录用户上传照片，支持单张上传和批量上传。 {@link https://wiki.connect.qq.com/upload_pic}
	 * {@link https://wiki.connect.qq.com/upload_pic}
	 * 
	 * @param request
	 * @return
	 */
	public UploadPicResponse uploadPic(UploadPicRequest request) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("photodesc", request.getPhotodesc());
		params.put("title", request.getTitle());
		params.put("albumid", request.getAlbumid());
		params.put("mobile", request.getMobile());
		params.put("x", request.getX());
		params.put("y", request.getY());
		params.put("picture", request.getPicture());
		params.put("needfeed", request.getNeedfeed());
		params.put("successnum", request.getSuccessnum());
		params.put("picnum", request.getPicnum());

		JsonObject response = doPost(UPLOAD_PIC, request, params, MediaType.MULTIPART_FORM_DATA);
		return new UploadPicResponse(response);
	}

	/**
	 * 登录用户创建相册。注：每个用户最多可创建10个相册。
	 * 
	 * {@link https://wiki.connect.qq.com/add_album}
	 * 
	 * @param request
	 * @return
	 */
	public AddAlbumResponse addAlbum(AddAlbumRequest request) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("albumname", request.getAlbumname());
		params.put("albumdesc", request.getAlbumdesc());
		params.put("priv", request.getPriv().getPriv());
		params.put("question", request.getPriv().getQuestion());
		params.put("answer", request.getPriv().getAnswer());
		JsonObject response = doPost(ADD_ALBUM, request, params, MediaType.APPLICATION_FORM_URLENCODED);
		return new AddAlbumResponse(response);
	}

	/**
	 * 获取登录用户的照片列表。 {@link https://wiki.connect.qq.com/list_photo}
	 * 
	 * {@link https://wiki.connect.qq.com/list_photo}
	 * 
	 * @param request
	 * @return
	 */
	public ListPhotoResponse listPhoto(ListPhotoRequest request) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("albumname", request.getAlbumid());
		JsonObject response = doGet(LIST_PHOTO, request, params);
		return new ListPhotoResponse(response);
	}
}
