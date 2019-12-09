package scw.lang;

public class ParameterVerifyException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String parameterName;

	public ParameterVerifyException(String parameterName) {
		super("[Verify failed] - argument [" + parameterName + "]");
		this.parameterName = parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
}
