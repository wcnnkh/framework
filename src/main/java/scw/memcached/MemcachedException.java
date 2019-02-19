package scw.memcached;

public class MemcachedException extends RuntimeException{
	private static final long serialVersionUID = -6858088801115238858L;
	
	public MemcachedException(){
		super();
	}
	
	public MemcachedException(Throwable e){
		super(e);
	}
}
