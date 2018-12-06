package shuchaowen.tencent.weixin.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import shuchaowen.connection.http.HttpUtils;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.StringUtils;
import shuchaowen.tencent.weixin.WeiXinUtils;

public class WebUserInfo implements Serializable {
	private static final String weixin_get_web_userinfo = "https://api.weixin.qq.com/sns/userinfo";
	
	private static final long serialVersionUID = 1L;
	private String openid;//用户的唯一标识
	private String nickname;//用户昵称
	private int sex;//用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
	private String province;//用户个人资料填写的省份
	private String city;//普通用户个人资料填写的城市
	private String country;//国家，如中国为CN
	/**
	 * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像）， 用户没有头像时该项为空。
	 * 若用户更换头像，原有头像URL将失效。
	 */
	private String headimgurl;
	private String privilege;// 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
	private String unionid;// 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。
	
	/**
	 * 用于序列化
	 */
	public WebUserInfo(){}
	
	public WebUserInfo(String appid, String appsecret, String code){
		load(appid, appsecret, code);
	}
	
	public WebUserInfo(String openid, String user_access_token){
		load(openid, user_access_token);
	}
	
	public void load(String appid, String appsecret, String code){
		WebUserAccesstoken webUserAccesstoken = new WebUserAccesstoken(appid, appsecret, code);
		load(webUserAccesstoken.getOpenid(), webUserAccesstoken.getAccess_token());
	}
	
	public void load(String openid, String user_access_token){
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("access_token", user_access_token);
		paramMap.put("openid", openid);
		paramMap.put("lang", "zh_CN");
		String content = HttpUtils.doPost(weixin_get_web_userinfo, paramMap);
		if(StringUtils.isNull(content)){
			throw new ShuChaoWenRuntimeException("无法获取userinfo");
		}
		
		JSONObject jsonObject = JSONObject.parseObject(content);
		if(WeiXinUtils.isError(jsonObject)){
			throw new ShuChaoWenRuntimeException(content);
		}
		
		this.openid = jsonObject.getString("openid");
		this.nickname = jsonObject.getString("nickname");
		this.sex = jsonObject.getIntValue("sex");
		this.province = jsonObject.getString("province");
		this.city = jsonObject.getString("city");
		this.country = jsonObject.getString("country");
		this.headimgurl = jsonObject.getString("headimgurl");
		this.privilege = jsonObject.getString("privilege");
		this.unionid = jsonObject.getString("unionid");
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getPrivilege() {
		return privilege;
	}

	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
}
