package shuchaowen.core.transaction;

public abstract class AbstractTransaction implements Transaction{
	
	public void execute() throws Exception{
		transaction(this);
	}
	
	public static void transaction(Transaction transaction) throws Exception{
		try {
			transaction.begin();
		} catch (Exception e) {
			try {
				transaction.end();
			} catch (Exception e1) {
				throw e1;
			}
			throw e;
		}
		
		try {
			transaction.process();
		} catch (Exception e) {
			try {
				transaction.rollback();
			} catch (Exception e1) {
				throw e1;
			}
			throw e;
		}finally{
			try {
				transaction.end();
			} catch (Exception e) {
				throw e;
			}
		}
	}
}
