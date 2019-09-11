package scw.security.signature.impl;

public class MD5StringSignatureServiceImpl extends StringSignatureServiceImpl {

	public MD5StringSignatureServiceImpl(String charsetName) {
		super(charsetName, "MD5");
	}

}
