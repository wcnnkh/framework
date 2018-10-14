package shuchaowen.core.db.transaction;

public abstract class AbstractTransaction implements Transaction{
	
	public void execute() throws Exception{
		try {
			begin();
		} catch (Exception e) {
			try {
				end();
			} catch (Exception e1) {
				throw e1;
			}
			throw e;
		}
		
		try {
			process();
		} catch (Exception e) {
			try {
				rollback();
			} catch (Exception e1) {
				throw e1;
			}
			throw e;
		}finally{
			try {
				end();
			} catch (Exception e) {
				throw e;
			}
		}
	}
}
