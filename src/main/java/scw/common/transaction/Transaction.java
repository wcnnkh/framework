package scw.common.transaction;


public interface Transaction{
	void begin() throws Exception;
	
	void process() throws Exception;
	
	void end() throws Exception;
	
	void rollback() throws Exception;
}
