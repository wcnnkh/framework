package io.basc.framework.util;

/**
 * 
 * B b = replay(capture());
 * try{
 * codeing... 
 * }
 * finally{ 
 * restore(b); 
 * }
 * 
 * @author wcnnkh
 *
 * @param <A>
 * @param <B>
 */
public interface Inheriter<A, B> {
	A capture();

	B replay(A capture);

	void restore(B backup);

	B clear();
}