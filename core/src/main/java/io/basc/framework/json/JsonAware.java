package io.basc.framework.json;

/**
 * 支持JSON文本定制输出的bean应该实现这个接口。
 * 
 * @author wcnnkh
 *
 */
public interface JsonAware {
	String toJsonString();
}
