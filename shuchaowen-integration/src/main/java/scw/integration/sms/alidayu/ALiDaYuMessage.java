package scw.integration.sms.alidayu;

import java.io.Serializable;
import java.util.Map;

public class ALiDaYuMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private ALiDaYuMessageModel messageModel;
	private Map<String, String> sms_param;

	public ALiDaYuMessage(ALiDaYuMessageModel messageModel, Map<String, String> sms_param) {
		this.messageModel = messageModel;
		this.sms_param = sms_param;
	}

	public ALiDaYuMessageModel getMessageModel() {
		return messageModel;
	}

	public void setMessageModel(ALiDaYuMessageModel messageModel) {
		this.messageModel = messageModel;
	}

	public Map<String, String> getSms_param() {
		return sms_param;
	}

	public void setSms_param(Map<String, String> sms_param) {
		this.sms_param = sms_param;
	}
}
