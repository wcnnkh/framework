package scw.alibaba.dayu;

import scw.util.result.Result;

public interface AliDaYu {
	Result sendMessage(MessageModel messageModel, String sms_param, String toPhones);
}
