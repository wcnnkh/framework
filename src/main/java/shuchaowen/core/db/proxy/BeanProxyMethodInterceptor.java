package shuchaowen.core.db.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import shuchaowen.core.db.ColumnInfo;
import shuchaowen.core.db.DB;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;

public class BeanProxyMethodInterceptor implements MethodInterceptor, BeanProxy{
	private static final long serialVersionUID = 1L;
	private transient TableInfo tableInfo;
	private transient Map<String, Object> changeColumnMap;
	private boolean startListen = false;

	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		if (args.length == 0) {
			if (BeanProxy.START_LISTEN.equals(method.getName())) {
				startListen();
				return null;
			} else if (BeanProxy.GET_CHANGE_COLUMN_MAP.equals(method.getName())) {
				return getChange_ColumnMap();
			}
		}

		if (startListen) {
			if(tableInfo == null){
				tableInfo = DB.getTableInfo(obj.getClass());
			}
			
			ColumnInfo columnInfo = tableInfo.getColumnByNotPrimaryKeySetterNameMap(method.getName());
			if (columnInfo != null
					&& (changeColumnMap == null || !changeColumnMap
							.containsKey(columnInfo.getName()))) {
				Object oldValue = columnInfo.getValue(obj);
				Object rtn = invoker(obj, method, args, proxy);
				if(changeColumnMap == null){
					changeColumnMap = new HashMap<String, Object>();
				}
				changeColumnMap.put(columnInfo.getName(), oldValue);
				return rtn;
			}
		}
		
		return invoker(obj, method, args, proxy);
	}
	
	protected Object invoker(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable{
		return proxy.invokeSuper(obj, args);
	}

	public static <T> T newInstance(Class<T> type) {
		return newInstance(type, DB.getTableInfo(type));
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> type, TableInfo tableInfo) {
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(tableInfo.getProxyInterface());
		enhancer.setSerialVersionUID(serialVersionUID);
		enhancer.setCallback(new BeanProxyMethodInterceptor());
		enhancer.setSuperclass(type);
		return (T) enhancer.create();
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getCglibProxyBean(Class<T> type){
		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(TableInfo.getBeanProxyInterface(type));
		enhancer.setSerialVersionUID(serialVersionUID);
		enhancer.setCallbackType(BeanProxyMethodInterceptor.class);
		enhancer.setSuperclass(type);
		return enhancer.createClass();
	}
	
	public static void registerCglibProxyBean(Class<?> type){
		Class<?> clz = getCglibProxyBean(type);
		Logger.info("CGLIB", "register proxy class[" + clz.getName() + "]");
	}
	
	public static void registerCglibProxyTableBean(String pageName){
		for(Class<?> clz : ClassUtils.getClasses(pageName)){
			Table table = clz.getAnnotation(Table.class);
			if(table == null){
				continue;
			}
			registerCglibProxyBean(clz);
		}
	}

	public void startListen() {
		if(changeColumnMap != null && !changeColumnMap.isEmpty()){
			changeColumnMap.clear();
		}
		startListen = true;
	}

	public Map<String, Object> getChange_ColumnMap() {
		return changeColumnMap;
	}
}