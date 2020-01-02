package scw.integration.verification.support;

import java.util.Arrays;

import scw.data.TemporaryCache;
import scw.integration.sms.ShortMessageService;
import scw.integration.verification.AbstractVerificationCodeService;
import scw.integration.verification.VerificationCodeException;
import scw.util.PhoneNumber;

public class SmsVerificationCodeService<M> extends AbstractVerificationCodeService<PhoneNumber> {
	private MessageFactory<M, PhoneNumber> messageFactory;
	private ShortMessageService<M, ?> shortMessageService;

	public SmsVerificationCodeService(ShortMessageService<M, ?> shortMessageService,
			MessageFactory<M, PhoneNumber> messageFactory, TemporaryCache temporaryCache, int maxCount,
			int maxCountTimeout, int maxTimeInterval, int maxActiveTime, String prefix, int maxCheckCount) {
		super(temporaryCache, maxCount, maxCountTimeout, maxTimeInterval, maxActiveTime, prefix, maxCheckCount);
		this.messageFactory = messageFactory;
		this.shortMessageService = shortMessageService;
	}

	@Override
	protected void sendVerificationCode(int type, PhoneNumber phoneNumber, String code)
			throws VerificationCodeException {
		M message = messageFactory.generatorMessage(type, code, phoneNumber);
		if (message == null) {
			throw new NullPointerException("找不到指定的message");
		}

		shortMessageService.send(message, Arrays.asList(phoneNumber));
	}
}
