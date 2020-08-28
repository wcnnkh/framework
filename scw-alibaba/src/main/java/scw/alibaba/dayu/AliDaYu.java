package scw.alibaba.dayu;

import scw.result.Result;

public interface AliDaYu {
	Result sendMessage(MessageModel messageModel, String sms_param, String toPhones);
}
