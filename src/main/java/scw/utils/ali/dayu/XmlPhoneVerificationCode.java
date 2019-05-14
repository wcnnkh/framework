package scw.utils.ali.dayu;

import java.util.Map;

import scw.result.DataResult;

public interface XmlPhoneVerificationCode extends AliDaYu{
	DataResult<String> sendMessage(int configIndex, String sms_param, String toPhones);
	
	/**
	 * 发送验证码
	 * @param configIndex
	 * @param phone
	 * @param parameterMap
	 * @return
	 */
	DataResult<String> sendCode(int configIndex, String phone, Map<String, String> parameterMap);
	
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
	<T> DataResult<T> canSend(int configIndex, String phone);
}
