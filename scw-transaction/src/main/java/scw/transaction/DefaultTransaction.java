package scw.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Assert;
import scw.core.OrderComparator;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

/**
 * 默认的事务实现<br/>
 * //TODO 是否应该重试？目前仅定义为事务载体，重试机制由使用方实现 
 * @author shuchaowen
 *
 */
public final class DefaultTransaction implements Transaction, TransactionResource{
	private static Logger logger = LoggerFactory.getLogger(DefaultTransaction.class);
	private final DefaultTransaction parent;
	private final boolean active;
	private final boolean isNew;
	private final TransactionDefinition definition;

	private Map<Object, Object> resourceMap;
	private List<TransactionLifecycle> transactionLifecycles;
	private boolean complete;
	private boolean rollbackOnly;// 此事务直接回滚，不再提供服务
	private boolean commit;// 是否已经提交
	private Savepoint savepoint;

	/**
	 * 创建一个新的事务
	 * 
	 * @param parent
	 * @param definition
	 * @param active
	 */
	public DefaultTransaction(DefaultTransaction parent, TransactionDefinition definition, boolean active) {
		this(parent, definition, active, true, null);
	}
	
	/**
	 * 包装旧的事务
	 * 
	 * @param parent
	 * @param definition
	 */
	public DefaultTransaction(DefaultTransaction parent, TransactionDefinition definition) {
		this(parent, definition, parent.isActive(), false, null);
	}
	
	/**
	 * 嵌套事务
	 * @param parent
	 * @param definition
	 * @param savepoint
	 */
	public DefaultTransaction(DefaultTransaction parent, TransactionDefinition definition, Savepoint savepoint){
		this(parent, definition, parent.isActive(), true, savepoint);
	}
	
	/**
	 * 自定义
	 * @param parent
	 * @param definition
	 * @param active
	 * @param isNew
	 * @param savepoint
	 */
	public DefaultTransaction(DefaultTransaction parent, TransactionDefinition definition, boolean active, boolean isNew, @Nullable Savepoint savepoint){
		Assert.isTrue(!(!isNew && parent == null), "An old transaction must have a parent(一个旧的事务一定存在父级)");
		this.parent = parent;
		this.active = active;
		this.isNew = isNew;
		this.definition = definition;
		this.savepoint = savepoint;
		
		/**
		 * 如果当前是一个嵌套事务，或者父级是一个嵌套事务，那么应该受父级管理
		 */
		if(parent != null && (savepoint != null || parent.hasSavepoint())){
			parent.addSynchronization(this);
		}
	}
	
	public boolean hasSavepoint(){
		return savepoint != null;
	}
	
	/**
	 * 是否已经提交
	 * 
	 * @return
	 */
	public boolean isCommit() {
		return isNew() ? commit : parent.commit;
	}

	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	public boolean setRollbackOnly(boolean rollbackOnly) {
		this.rollbackOnly = rollbackOnly;
		return true;
	}

	public boolean isNew() {
		return isNew;
	}

	public boolean isActive() {
		return active;
	}

	public TransactionDefinition getDefinition() {
		return definition;
	}

	public DefaultTransaction getParent() {
		return parent;
	}

	public boolean isCompleted() {
		return complete;
	}
	
	private void checkStatus() {
		if (complete) {
			throw new TransactionException("当前事务已经结束，无法进行后序操作");
		}

		if (rollbackOnly) {// 当前事务应该直接回滚，不能继续操作了
			throw new TransactionException("当前事务已设置为回滚，无法进行后序操作");
		}

		if (commit) {
			throw new TransactionException("当前事务已经提交，无法进行后序操作");
		}
	}

	public void addLifecycle(TransactionLifecycle tlc) {
		Assert.requiredArgument(tlc != null, "transactionLifecycle");
		checkStatus();

		if (!isNew) {
			parent.addLifecycle(tlc);
			return;
		}

		if(transactionLifecycles == null){
			transactionLifecycles = new LinkedList<TransactionLifecycle>();
		}
		transactionLifecycles.add(tlc);
	}

	@SuppressWarnings("unchecked")
	public <T> T getResource(Object name) {
		if (!isNew) {
			return parent.getResource(name);
		}

		return (T) (resourceMap == null ? null : resourceMap.get(name));
	}

	@SuppressWarnings("unchecked")
	public <T> T bindResource(Object name, T resource) {
		checkStatus();

		if (!isNew) {
			return parent.bindResource(name, resource);
		}

		Object resourceToUse = null;
		if (resourceMap == null) {
			resourceMap = new HashMap<Object, Object>(4);
		} else {
			resourceToUse = resourceMap.get(name);
		}
		
		if(resourceToUse == null){
			resourceMap.put(name, resource);
			resourceToUse = resource;
		}
		return (T) resourceToUse;
	}
	
	private boolean init;
	private List<TransactionSynchronization> synchronizations;
	
	public void addSynchronization(TransactionSynchronization transactionSynchronization){
		checkStatus();
		if(synchronizations == null){
			synchronizations = new ArrayList<TransactionSynchronization>(8);
		}
		synchronizations.add(transactionSynchronization);
	}
	
	private void init(){
		if(init){
			//已经初始化过了
			return ;
		}
		
		init = true;
		if(resourceMap == null){
			this.synchronizations = Collections.emptyList();
		} else {
			if(synchronizations == null){
				synchronizations = new ArrayList<TransactionSynchronization>(resourceMap.size());
			}
			
			for (Entry<Object, Object> entry : resourceMap.entrySet()) {
				Object resource = entry.getValue();
				if (resource instanceof TransactionSynchronization) {
					synchronizations.add((TransactionSynchronization) resource);
				}
			}
		}
		
		if(synchronizations.isEmpty()){
			synchronizations = Collections.emptyList();
		}else{
			synchronizations.sort(OrderComparator.INSTANCE);
			synchronizations = Collections.unmodifiableList(synchronizations);
		}
		
		if(transactionLifecycles == null){
			transactionLifecycles = Collections.emptyList();
		}else{
			transactionLifecycles.sort(OrderComparator.INSTANCE);
			transactionLifecycles = Collections.unmodifiableList(transactionLifecycles);
		}
		
		if(resourceMap == null){
			resourceMap = Collections.emptyMap();
		}else{
			resourceMap = Collections.unmodifiableMap(resourceMap);
		}
	}
	
	public Savepoint createSavepoint() throws TransactionException {
		if(resourceMap == null){
			return new EmptySavepoint();
		}
		
		List<Savepoint> savepoints = new ArrayList<Savepoint>(resourceMap.size());
		for (Entry<Object, Object> entry : resourceMap.entrySet()) {
			Object resource = entry.getValue();
			if (resource instanceof TransactionResource) {
				savepoints.add(((TransactionResource) resource).createSavepoint());
			}
		}
		
		if(savepoints.isEmpty()){
			return new EmptySavepoint();
		}
		
		return new MultipleSavepoint(savepoints);
	}

	public void commit() throws Throwable {
		if (isCompleted()) {
			return;
		}

		if (isRollbackOnly()) {
			return;
		}

		if (!isNew()) {
			return;
		}

		commit = true;
		init();
		
		for (TransactionLifecycle lifeCycle : transactionLifecycles) {
			lifeCycle.beforeCommit();
		}
		
		Iterator<TransactionSynchronization> iterator = synchronizations.iterator();
		while (iterator.hasNext()) {
			TransactionSynchronization transaction = iterator.next();
			if (transaction != null) {
				transaction.commit();
			}
		}
		
		for (TransactionLifecycle lifeCycle : transactionLifecycles) {
			try {
				lifeCycle.afterCommit();
			} catch (Throwable e) {
				logger.error(e, "AfterCommit transaction [{}] lifecycle[{}]", this, lifeCycle);
			}
		}
	}

	public void rollback() throws TransactionException {
		if (complete) {
			return;
		}

		if (!isNew) {
			return;
		}

		commit = true;
		init();
		
		for (TransactionLifecycle lifeCycle : transactionLifecycles) {
			try {
				lifeCycle.beforeRollback();
			} catch (Throwable e) {
				logger.error(e, "BeforeRollback transaction [{}] lifecycle[{}]", this, lifeCycle);
			}
		}
		
		if(savepoint != null){
			try {
				savepoint.rollback();
			} catch (Throwable e) {
				logger.error(e, "Rollback savepoint transaction [{}] savepoint[{}]", this, savepoint);
			}
		}
		
		ListIterator<TransactionSynchronization> iterator = synchronizations.listIterator(synchronizations.size());
		while (iterator.hasPrevious()) {
			TransactionSynchronization synchronization = iterator.previous();
			if (synchronization != null) {
				try {
					synchronization.rollback();
				} catch (Throwable e) {
					logger.error(e, "Rollback transaction [{}] synchronization[{}]", this, synchronization);
				}
			}
		}
		
		for (TransactionLifecycle lifeCycle : transactionLifecycles) {
			try {
				lifeCycle.afterRollback();
			} catch (Throwable e) {
				logger.error(e, "AfterRollback transaction [{}] lifecycle[{}]", this, lifeCycle);
			}
			
		}
	}

	public void complete() {
		if (complete) {
			return;
		}

		if (!isNew) {
			return;
		}
		
		complete = true;
		init();
		
		if(savepoint != null){
			try {
				savepoint.release();
			} catch (Throwable e) {
				logger.error(e, "Complete savepoint transaction [{}] savepoint[{}]", this, savepoint);
			}
		}
		
		ListIterator<TransactionSynchronization> iterator = synchronizations.listIterator(synchronizations.size());
		while (iterator.hasPrevious()) {
			TransactionSynchronization synchronization = iterator.previous();
			if (synchronization != null) {
				try {
					synchronization.complete();
				} catch (Throwable e) {
					logger.error(e, "Complete transaction [{}] synchronization[{}]", this, synchronization);
				}
			}
		}
		
		for (TransactionLifecycle lifeCycle : transactionLifecycles) {
			try {
				lifeCycle.complete();
			} catch (Throwable e) {
				logger.error(e, "Complete transaction [{}] lifeCycle[{}]", this, lifeCycle);
			}
		}
	}
}
