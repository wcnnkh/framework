package scw.aop.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import scw.aop.MethodInterceptor;

public class ConfigurableMethodInterceptor extends AbstractMethodInterceptors{
	private static final long serialVersionUID = 1L;
	private volatile List<MethodInterceptor> interceptors;

	public Iterator<MethodInterceptor> iterator() {
		if(interceptors == null){
			return Collections.emptyIterator();
		}
		return interceptors.iterator();
	}
	
	public int size(){
		return interceptors == null? 0:interceptors.size();
	}
	
	public boolean isEmpty(){
		return interceptors == null || interceptors.isEmpty();
	}
	
	private void init(){
		if(interceptors == null){
			synchronized (this) {
				if(interceptors == null){
					interceptors = new CopyOnWriteArrayList<MethodInterceptor>();
				}
			}
		}
	}
	
	public void addMethodInterceptor(MethodInterceptor methodInterceptor){
		init();
		this.interceptors.add(methodInterceptor);
	}
	
	public void addFirstMethodInterceptor(MethodInterceptor methodInterceptor){
		init();
		this.interceptors.add(0, methodInterceptor);
	}
}
