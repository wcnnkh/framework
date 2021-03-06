package scw.codec;

public interface Validator<D, E> {
	boolean verify(D source, E encode) throws CodecException;
}
