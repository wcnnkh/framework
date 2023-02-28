package io.basc.framework.util;

/**
 * B b = replay(capture());<br/>
 * try{ <br/>
 * codeing... <br/>
 * }<br/>
 * finally{ <br/>
 * restore(b); <br/>
 * }<br/>
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