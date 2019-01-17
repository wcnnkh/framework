package scw.utils.tencent.weixin.miniprogram.process;

import com.alibaba.fastjson.JSONObject;

import scw.utils.tencent.weixin.WeiXinProcess;

/**
 * 创建被分享动态消息的 activity_id
 * @author shuchaowen
 *
 */
public final class CreateActivityId extends WeiXinProcess{
	private static final String API = "https://api.weixin.qq.com/cgi-bin/message/wxopen/activityid/create?access_token=";
	private String activity_id;
	private Long expiration_time; 
	
	public CreateActivityId(String access_token){
		JSONObject json = post(API + access_token, null);
		if(isSuccess()){
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
