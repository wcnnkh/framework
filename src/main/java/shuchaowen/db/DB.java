package shuchaowen.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.common.utils.ClassUtils;
import shuchaowen.db.sql.SQLFormat;
import shuchaowen.db.storage.CommonStorage;
import shuchaowen.db.storage.Storage;

public abstract class DB extends AbstractDB {
	private Map<String, Storage> storageMap = new HashMap<String, Storage>();
	private Storage storage;

	public DB() {
		super(null);
		this.storage = new CommonStorage(this);
	}

	public DB(Storage storage, SQLFormat sqlFormat) {
		super(sqlFormat);
		this.storage = storage;
	}

	public synchronized void registerStorage(Storage storage, Class<?>... tableClass) {
		for (Class<?> clz : tableClass) {
			storageMap.put(ClassUtils.getProxyRealClassName(clz), storage);
		}
	}

	public synchronized void removeAllStorage() {
		if (storageMap != null) {
			storageMap.clear();
		}
	}

	public synchronized void removeStorage(Class<?>... tableClass) {
		for (Class<?> clz : tableClass) {
			storageMap.remove(ClassUtils.getProxyRealClassName(clz));
		}
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public Storage getStorage(Class<?> tableClass) {
		Storage storage = storageMap.get(ClassUtils.getProxyRealClassName(tableClass));
		return storage == null ? this.storage : storage;
	}

	// storage
	public <T> T getById(Class<T> type, Object... params) {
		return getStorage(type).getById(type, params);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return getStorage(type).getByIdList(type, params);
	}

	/** 保存 **/
	public void save(Object... beans) {
		save(Arrays.asList(beans));
	}

	public void save(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.SAVE, bean));
		}

		op(operationBeans);
	}

	/** 删除 **/
	public void delete(Object... beans) {
		delete(Arrays.asList(beans));
	}

	public void delete(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.DELETE, bean));
		}

		op(operationBeans);
	}

	/** 更新 **/
	public void update(Object... beans) {
		update(Arrays.asList(beans));
	}

	public void update(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.UPDATE, bean));
		}

		op(operationBeans);
	}

	/** 保存或更新 **/
	public void saveOrUpdate(Object... beans) {
		saveOrUpdate(Arrays.asList(beans));
	}

	public void saveOrUpdate(Collection<?> beans) {
		if (beans == null || beans.isEmpty()) {
			return;
		}

		List<OperationBean> operationBeans = new ArrayList<OperationBean>(beans.size());
		for (Object bean : beans) {
			if (bean == null) {
				continue;
			}

			operationBeans.add(new OperationBean(OperationType.SAVE_OR_UPDATE, bean));
		}

		op(operationBeans);
	}

	public void op(Collection<OperationBean> operationBeans) {
		if (operationBeans == null || operationBeans.isEmpty()) {
			return;
		}

		if (storageMap.isEmpty()) {
			storage.op(operationBeans);
		} else if (operationBeans.size() == 1) {
			for (OperationBean bean : operationBeans) {
				getStorage(bean.getBean().getClass()).op(operationBeans);
				break;
			}
		} else {
			Map<Storage, Collection<OperationBean>> map = new HashMap<Storage, Collection<OperationBean>>();
			for (OperationBean bean : operationBeans) {
				if (bean == null || bean.getBean() == null) {
					continue;
				}

				Storage storage = getStorage(bean.getBean().getClass());
				Collection<OperationBean> collection = map.get(storage);
				if (collection == null) {
					collection = new ArrayList<OperationBean>();
					collection.add(bean);
					map.put(storage, collection);
				} else {
					collection.add(bean);
				}
			}

			for (Entry<Storage, Collection<OperationBean>> entry : map.entrySet()) {
				entry.getKey().op(entry.getValue());
			}
		}
	}
}