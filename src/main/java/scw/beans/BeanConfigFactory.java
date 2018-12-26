package scw.beans;

import java.util.Map;

public interface BeanConfigFactory {
	/**
	 * ID对应的bean
	 * @return
	 */
	Map<String, Bean> getBeanMap();

	/**
	 * 名称映射关系
	 * @return
	 */
	Map<String, String> getNameMappingMap();
}
