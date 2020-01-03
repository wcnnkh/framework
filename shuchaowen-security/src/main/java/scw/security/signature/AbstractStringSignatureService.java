package scw.security.signature;

public abstract class AbstractStringSignatureService implements StringSignatureService {
	private final String charsetName;

	public AbstractStringSignatureService(String charsetName) {
		this.charsetName = charsetName;
	}

	public String getCharsetName() {
		return charsetName;
	}
}
