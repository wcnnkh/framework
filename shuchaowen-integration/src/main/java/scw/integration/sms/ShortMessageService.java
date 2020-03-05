package scw.integration.sms;

import java.util.Collection;

import scw.util.phone.PhoneNumber;

/**
 * 短讯服务
 * 
 * @author shuchaowen
 *
 * @param <V>
 */
public interface ShortMessageService<M, V> {
	/**
	 * @param message
	 * @param phoneNumbers
	 * @return
	 * @throws SmsException
	 */
	V send(M message, Collection<PhoneNumber> phoneNumbers) throws SmsException;
}
