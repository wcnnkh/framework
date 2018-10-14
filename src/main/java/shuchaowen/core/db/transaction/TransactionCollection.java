package shuchaowen.core.db.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class TransactionCollection extends AbstractTransaction{
	private Collection<Transaction> collection;
	private int beginTag = 0;
	private int processTag = 0;
	
	public TransactionCollection(){
	}
	
	public TransactionCollection(Collection<Transaction> collection) {
		this.collection = collection;
	}
	
	public TransactionCollection add(Transaction transaction){
		if(transaction == null){
			return this;
		}
		
		if(collection == null){
			collection = new ArrayList<Transaction>();
		}
		collection.add(transaction);
		return this;
	}
	
	public void clear(){
		if(collection != null){
			collection.clear();
		}

		beginTag = 0;
		processTag = 0;
	}
	
	public boolean isEmpty(){
		return collection == null || collection.isEmpty();
	}

	public void begin() throws Exception {
		if(collection != null){
			Iterator<Transaction> iterator = collection.iterator();
			for(; iterator.hasNext(); beginTag ++){
				Transaction transaction = iterator.next();
				if(transaction != null){
					transaction.begin();
				}
			}
		}
	}

	public void process() throws Exception {
		if(collection != null){
			Iterator<Transaction> iterator = collection.iterator();
			for(; iterator.hasNext(); processTag ++){
				Transaction transaction = iterator.next();
				if(transaction != null){
					transaction.process();
				}
			}
		}
	}

	public void end() throws Exception {
		if(collection != null){
			Iterator<Transaction> iterator = collection.iterator();
			for(; beginTag >= 0 && iterator.hasNext(); beginTag --){
				Transaction transaction = iterator.next();
				if(transaction != null){
					try {
						transaction.end();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void rollback() throws Exception {
		if(collection != null){
			Iterator<Transaction> iterator = collection.iterator();
			for(;processTag >= 0 && iterator.hasNext(); processTag --){
				Transaction transaction = iterator.next();
				if(transaction != null){
					try {
						transaction.rollback();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
