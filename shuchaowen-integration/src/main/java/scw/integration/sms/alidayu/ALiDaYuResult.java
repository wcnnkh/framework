package scw.integration.sms.alidayu;

import scw.json.JsonObject;
import scw.json.JSONUtils;
import scw.result.support.AbstractResult;

/**
 * 发送短信返回结果
 * 
 * @author shuchaowen
 *
 */
public final class ALiDaYuResult extends AbstractResult {
	private static final long serialVersionUID = 1L;
	private int code;
	private String model;
	private String msg;
	private String request_id;
	private String sub_code;

	public ALiDaYuResult(String jsonContent) {
		JsonObject jsonObject = JSONUtils.parseObject(jsonContent);
		if (jsonObject.containsKey("alibaba_aliqin_fc_sms_num_send_response")) {// 成功
			jsonObject = jsonObject.getJsonObject("alibaba_aliqin_fc_sms_num_send_response");
			this.request_id = jsonObject.getString("request_id");
			jsonObject = jsonObject.getJsonObject("result");
			this.code = jsonObject.getIntValue("err_code");
			this.model = jsonObject.getString("model");
			this.msg = jsonObject.getString("msg");
		} else {// 失败
			jsonObject = jsonObject.getJsonObject("error_response");
			this.code = jsonObject.getIntValue("code");
			this.sub_code = jsonObject.getString("sub_code");
			this.msg = jsonObject.getString("sub_msg");
			this.request_id = jsonObject.getString("request_id");
		}
	}

	public String getModel() {
		return model;
	}

	public String getMsg() {
		return msg;
	}

	public boolean isSuccess() {
		return code == 0;
	}
	
	public String getSub_code() {
		return sub_code;
	}

	public String getRequest_id() {
		return request_id;
	}

	public int getCode() {
		return code;
	}
}
