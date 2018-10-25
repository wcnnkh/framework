package shuchaowen.core.db.proxy;

import shuchaowen.core.beans.BeanUtils;
import shuchaowen.core.db.DB;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;

public class BeanProxyUtils{
	public static <T> T newInstance(Class<T> type) {
		return newInstance(type, DB.getTableInfo(type));
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> type, TableInfo tableInfo) {
		return (T) BeanUtils.getEnhancer(type, null, null).create();
	}
	
	public static <T> Class<T> getCglibProxyBean(Class<T> type){
		return BeanUtils.getEnhancerClass(type);
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
}