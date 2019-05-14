package scw.utils.ali.dayu;

import scw.result.DataResult;

public interface AliDaYu {
	DataResult<String> sendMessage(MessageModel messageModel, String sms_param, String toPhones);
}
