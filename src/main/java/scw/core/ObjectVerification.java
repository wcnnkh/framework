package scw.core;

public class ObjectVerification implements Verification<Object>{

	public boolean verification(Object data) {
		return data != null;
	}

}
