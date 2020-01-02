package scw.integration.ali.dayu;

import java.util.Map;

import scw.result.Result;

public interface XmlPhoneVerificationCode extends AliDaYu{
	Result sendMessage(int configIndex, String sms_param, String toPhones);
	
	/**
	 * 发送验证码
	 * @param configIndex
	 * @param phone
	 * @param parameterMap
	 * @return
	 */
	Result sendCode(int configIndex, String phone, Map<String, String> parameterMap);
	
	/**
	 * 检查验证码是否有效
	 * 
	 * @param configIndex
	 * @param phone
	 * @param code
	 * @duplicateCheck 是否可以重复使用
	 * @return
	 */
	boolean checkCode(int configIndex, String phone, String code, boolean duplicateCheck);

	/**
	 * 检查是否可以发送
	 * 
	 * @param configIndex
	 * @param phone
	 * @return
	 */
	Result canSend(int configIndex, String phone);
}
