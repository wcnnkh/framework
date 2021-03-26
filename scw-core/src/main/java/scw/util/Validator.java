package scw.util;


public interface Validator<D, E> {
	boolean verify(D source, E encode);
}
