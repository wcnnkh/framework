package scw.integration.sms.alidayu;

import scw.json.JSONUtils;
import scw.json.JsonObject;
import scw.util.result.Result;

/**
 * 发送短信返回结果
 * 
 * @author shuchaowen
 *
 */
public final class ALiDaYuResult extends Result {
	private static final long serialVersionUID = 1L;
	private String model;
	private String request_id;
	private String sub_code;

	public ALiDaYuResult(String jsonContent) {
		JsonObject jsonObject = JSONUtils.parseObject(jsonContent);
		if (jsonObject.containsKey("alibaba_aliqin_fc_sms_num_send_response")) {// 成功
			jsonObject = jsonObject.getJsonObject("alibaba_aliqin_fc_sms_num_send_response");
			this.request_id = jsonObject.getString("request_id");
			jsonObject = jsonObject.getJsonObject("result");
			setSuccess(true);
			setCode(jsonObject.getIntValue("err_code"));
			setMsg(jsonObject.getString("msg"));
			this.model = jsonObject.getString("model");
		} else {// 失败
			jsonObject = jsonObject.getJsonObject("error_response");
			setSuccess(false);
			setCode(jsonObject.getIntValue("code"));
			setMsg(jsonObject.getString("sub_msg"));
			this.sub_code = jsonObject.getString("sub_code");
			this.request_id = jsonObject.getString("request_id");
		}
	}

	public String getModel() {
		return model;
	}

	public String getSub_code() {
		return sub_code;
	}

	public String getRequest_id() {
		return request_id;
	}
}
