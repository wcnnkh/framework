package scw.context;

public interface NestingContext extends Context {
	NestingContext getParentContext();
	
	boolean isNewContext();
}
