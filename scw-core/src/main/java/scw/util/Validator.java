package scw.util;

import scw.codec.CodecException;

public interface Validator<D, E> {
	boolean verify(D source, E encode) throws CodecException;
}
