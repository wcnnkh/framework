package scw.ali.dayu;

import java.io.Serializable;

/**
 * 阿里大鱼短信模版
 * 
 * @author shuchaowen
 *
 */
public class MessageModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private int modelId;
	private String sms_free_sign_name;
	private String sms_template_name;
	private String sms_template_code;
	private String mode_content;

	/**
	 * 用于序列化
	 */
	protected MessageModel() {
	}

	public MessageModel(int modelId, String sms_free_sign_name, String sms_template_name, String sms_template_code,
			String mode_content) {
		this.modelId = modelId;
		this.sms_free_sign_name = sms_free_sign_name;
		this.sms_template_name = sms_template_name;
		this.sms_template_code = sms_template_code;
		this.mode_content = mode_content;
	}

	public int getModelId() {
		return modelId;
	}

	public String getSms_free_sign_name() {
		return sms_free_sign_name;
	}

	public String getSms_template_name() {
		return sms_template_name;
	}

	public String getSms_template_code() {
		return sms_template_code;
	}

	public String getMode_content() {
		return mode_content;
	}
}
