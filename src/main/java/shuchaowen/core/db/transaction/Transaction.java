package shuchaowen.core.db.transaction;

import shuchaowen.core.db.transaction.exception.TransactionBeginException;
import shuchaowen.core.db.transaction.exception.TransactionEndException;
import shuchaowen.core.db.transaction.exception.TransactionProcessException;
import shuchaowen.core.db.transaction.exception.TransactionRollbackException;

public abstract class Transaction{
	public abstract void begin() throws Exception;
	
	public abstract void process() throws Exception;
	
	public abstract void end() throws Exception;
	
	public abstract void rollback() throws Exception;
	
	public void execute(){
		try {
			begin();
		} catch (Exception e) {
			try {
				end();
			} catch (Exception e1) {
				throw new TransactionEndException(e1);
			}
			throw new TransactionBeginException(e);
		}
		
		try {
			 process();
		} catch (Exception e) {
			try {
				rollback();
			} catch (Exception e1) {
				throw new TransactionRollbackException(e1);
			}
			throw new TransactionProcessException(e);
		}finally{
			try {
				end();
			} catch (Exception e) {
				throw new TransactionEndException(e);
			}
		}
	}
}
