package scw.security.limit;

public interface SecurityLimitService {
	boolean tryLimit(String name);

	void reset(String name);
}
