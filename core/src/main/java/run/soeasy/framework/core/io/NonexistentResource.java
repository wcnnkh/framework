package run.soeasy.framework.core.io;

public class NonexistentResource implements Resource {
	public static final NonexistentResource NONEXISTENT_RESOURCE = new NonexistentResource();

	@Override
	public boolean exists() {
		return false;
	}
}
