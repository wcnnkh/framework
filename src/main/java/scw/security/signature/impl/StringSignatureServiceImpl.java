package scw.security.signature.impl;

import java.security.MessageDigest;

import scw.security.signature.SignatureUtils;
import scw.security.signature.StringSignatureService;

public class StringSignatureServiceImpl implements StringSignatureService {
	private String charsetName;
	private String type;

	public StringSignatureServiceImpl(String charsetName, String type) {
		this.charsetName = charsetName;
		this.type = type;
	}

	public String signature(String text) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance(type);
		messageDigest.reset();
		messageDigest.update(text.getBytes(charsetName));
		return SignatureUtils.byte2hex(messageDigest.digest());
	}

}
