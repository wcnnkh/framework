package scw.ali.dayu;

import scw.common.ProcessResult;

public interface AliDaYu {
	ProcessResult<String> sendMessage(MessageModel messageModel, String sms_param, String toPhones);
}
