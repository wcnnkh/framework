package scw.data.memcached;

public class MemcachedException extends RuntimeException{
	private static final long serialVersionUID = -6858088801115238858L;
	
	public MemcachedException(Throwable e){
		super(e);
	}
}
