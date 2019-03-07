package scw.transaction.tcc;

public interface TCCService {
	public void service(Object obj, InvokeInfo invokeInfo, String name);
}
