package scw.orm.cast;

import scw.orm.ORMException;

public class ValueCastException extends ORMException {
	private static final long serialVersionUID = 1L;

	public ValueCastException(String message, Throwable e) {
		super(message, e);
	}
}
