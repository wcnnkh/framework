package scw.security.signature.impl;

import java.security.MessageDigest;

import scw.security.signature.SignatureService;
import scw.security.signature.SignatureUtils;

public final class CommonSignatureServiceImpl implements SignatureService {
	private String charsetName;
	private String type;

	public CommonSignatureServiceImpl(String charsetName, String type) {
		this.charsetName = charsetName;
		this.type = type;
	}

	public String signature(String text) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance(type);
		messageDigest.update(text.getBytes(charsetName));
		return SignatureUtils.byte2hex(messageDigest.digest());
	}

}
