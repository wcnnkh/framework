package shuchaowen.excel.load;

import java.util.HashMap;
import java.util.Map.Entry;

import shuchaowen.core.exception.AlreadyExistsException;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.util.ClassInfo;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.FieldInfo;
import shuchaowen.core.util.StringUtils;

public abstract class AbstractLoadRow<T> implements LoadRow{
	private final int nameMappingIndex;
	private final int beginRowIndex;
	private final int endRowIndex;
	private final HashMap<String, Integer> nameMapping = new HashMap<String, Integer>();
	private final ClassInfo classInfo;
	
	/**
	 * @param type
	 * @param nameMappingIndex
	 * @param beginRowIndex
	 * @param endRowIndex 当此值为-1时不判断结束位置
	 */
	public AbstractLoadRow(Class<T> type, int nameMappingIndex, int beginRowIndex, int endRowIndex){
		this.nameMappingIndex = nameMappingIndex;
		this.beginRowIndex = beginRowIndex;
		this.endRowIndex = endRowIndex;
		this.classInfo = ClassUtils.getClassInfo(type);
	}

	public final ClassInfo getClassInfo() {
		return classInfo;
	}

	public final void load(int sheetIndex, int rowIndex, String[] contents) {
		if(rowIndex == nameMappingIndex){
			for(int i=0; i<contents.length; i++){
				String name = contents[i];
				if(StringUtils.isNull(name)){
					continue;
				}
				
				name = name.trim();
				
				if(nameMapping.containsKey(name)){
					throw new AlreadyExistsException(name);
				}
				
				nameMapping.put(name, i);
			}
		}else if(rowIndex >= beginRowIndex && (endRowIndex == -1 || rowIndex <= endRowIndex)){
			if(nameMapping.isEmpty()){
				throw new ShuChaoWenRuntimeException("未加载name的映射关系, nameMappingIndex=" + nameMappingIndex);
			}
			
			try {
				T obj = newInstance();	
				for(Entry<String, Integer> entry : nameMapping.entrySet()){
					FieldInfo fieldInfo = classInfo.getFieldInfo(entry.getKey());
					if(fieldInfo == null){
						continue;
					}
					
					fieldInfo.set(obj, contents[entry.getValue()]);
				}
				load(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public T newInstance() throws Exception{
		return (T) classInfo.getClz().newInstance();
	}
	
	public abstract void load(T row);
}
