package scw.util;

import java.util.Iterator;

public abstract class AbstractIterator<E> implements java.util.Iterator<E> {
	private Iterator<E> operableIterator;

	public void setOperableIterator(Iterator<E> operableIterator) {
		this.operableIterator = operableIterator;
	}

	public void remove() {
		if(operableIterator != null){
			operableIterator.remove();
			return ;
		}
		
		throw new UnsupportedOperationException("remove");
	}

}
