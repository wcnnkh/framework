package scw.utils.ali.dayu;

import scw.core.ProcessResult;

public interface AliDaYu {
	ProcessResult<String> sendMessage(MessageModel messageModel, String sms_param, String toPhones);
}
