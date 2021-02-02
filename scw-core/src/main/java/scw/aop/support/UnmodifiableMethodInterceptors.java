package scw.aop.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import scw.aop.MethodInterceptor;

public class UnmodifiableMethodInterceptors extends AbstractMethodInterceptors{
	private static final long serialVersionUID = 1L;
	private Iterable<MethodInterceptor> iterable;
	
	public UnmodifiableMethodInterceptors(Iterable<MethodInterceptor> iterable){
		this.iterable = iterable;
	}
	
	public UnmodifiableMethodInterceptors(MethodInterceptor ...methodInterceptors){
		this(Arrays.asList(methodInterceptors));
	}
	
	public Iterator<MethodInterceptor> iterator() {
		if(iterable == null){
			return Collections.emptyIterator();
		}
		
		return iterable.iterator();
	}

}
