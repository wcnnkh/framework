package scw.aop.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import scw.aop.MethodInterceptor;
import scw.util.MultiIterator;

public class ConfigurableMethodInterceptor extends AbstractMethodInterceptors{
	private static final long serialVersionUID = 1L;
	private volatile List<MethodInterceptor> interceptors;

	public Iterator<MethodInterceptor> iterator() {
		if(interceptors == null){
			return Collections.emptyIterator();
		}
		
		if(interceptors.size() == 1){
			return interceptors.iterator();
		}
		
		//不直接使用[interceptors.iterator()]的目的是为了降低方法调用的嵌套层级
		List<Iterator<MethodInterceptor>> iterators = new ArrayList<Iterator<MethodInterceptor>>(interceptors.size());
		for(MethodInterceptor interceptor : interceptors){
			if(interceptor == null){
				continue;
			}
			
			if(interceptor instanceof AbstractMethodInterceptors){
				iterators.add(((AbstractMethodInterceptors) interceptor).iterator());
			}else{
				iterators.add(Arrays.asList(interceptor).iterator());
			}
		}
		return new MultiIterator<MethodInterceptor>(iterators);
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
