package scw.compensat;


public interface Compensator extends Runnable{
	String getGroup();
	
	String getId();
	
	boolean isCancelled();
	
	boolean cancel();
	
	boolean isDone();
}
