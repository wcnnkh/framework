package scw.core;

public class StringEmptyVerification implements StringVerification {
	public static final StringEmptyVerification INSTANCE = new StringEmptyVerification();

	public boolean verification(CharSequence data) {
		return data == null || data.length() == 0;
	}

}
