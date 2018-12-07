package shuchaowen.common.transaction;

import shuchaowen.common.transaction.exception.TransactionBeginException;
import shuchaowen.common.transaction.exception.TransactionEndException;
import shuchaowen.common.transaction.exception.TransactionProcessException;
import shuchaowen.common.transaction.exception.TransactionRollbackException;

public abstract class AbstractTransaction implements Transaction{
	public void execute() {
		transaction(this);
	}
	
	public static void transaction(Transaction transaction){
		try {
			transaction.begin();
		} catch (Exception e) {
			try {
				transaction.end();
			} catch (Exception e1) {
				throw new TransactionEndException(e1);
			}
			throw new TransactionBeginException(e);
		}
		
		try {
			transaction.process();
		} catch (Exception e) {
			try {
				transaction.rollback();
			} catch (Exception e1) {
				throw new TransactionRollbackException(e1);
			}
			throw new TransactionProcessException(e);
		}finally{
			try {
				transaction.end();
			} catch (Exception e) {
				throw new TransactionEndException(e);
			}
		}
	}
}
