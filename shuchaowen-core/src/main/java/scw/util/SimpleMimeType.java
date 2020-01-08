package scw.util;

@Deprecated
public class SimpleMimeType extends MimeType {
	private static final long serialVersionUID = 1L;

	public SimpleMimeType(MimeType other, String charsetName) {
		super(other, charsetName);
	}
}
