package scw.xml;

public class XmlException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public XmlException(Throwable e) {
		super(e);
	}

	public XmlException(String message, Throwable e) {
		super(message, e);
	}
}
