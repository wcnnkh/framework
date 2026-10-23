package run.soeasy.framework.web;

import java.util.Map.Entry;

import org.springframework.http.MediaType;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.spi.ServiceMap;
import run.soeasy.framework.jackson.JsonFormat;
import run.soeasy.framework.jackson.XmlFormat;

/**
 * 媒体类型转换器注册表，管理{@link MediaType}与{@link Converter}的映射关系，实现{@link MediaTypeConverterFactory}接口。
 * 
 * <p>
 * 作为基于{@link ServiceMap}的容器，提供媒体类型与转换器的注册、查询功能，支持：
 * <ul>
 * <li>单例模式的系统默认实例（通过{@link #system()}获取）</li>
 * <li>默认注册常用转换器（JSON/XML）</li>
 * <li>兼容媒体类型查找（如application/*+json匹配application/json）</li>
 * </ul>
 * 
 * <p>
 * 核心作用是作为全局转换器映射中心，供HTTP客户端等组件根据媒体类型自动获取合适的转换器，
 * 实现数据的序列化（对象→媒体格式）与反序列化（媒体格式→对象）。
 * 
 * @see MediaTypeConverterFactory
 * @see MediaType
 * @see Converter
 * @see ServiceMap
 */
public class MediaTypeConverterRegistry extends ServiceMap<MediaType, Converter> implements MediaTypeConverterFactory {
	/** 系统默认注册表实例（单例），线程安全的延迟初始化 */
	private static volatile MediaTypeConverterRegistry system;

	/**
	 * 获取系统默认的媒体类型转换器注册表实例（单例）。
	 * <p>
	 * 采用双重检查锁定实现线程安全的延迟初始化，默认注册：
	 * <ul>
	 * <li>{@link MediaType#APPLICATION_JSON} →
	 * {@link JsonFormat#DEFAULT}（JSON转换器）</li>
	 * <li>{@link MediaType#APPLICATION_XML} →
	 * {@link XmlFormat#DEFAULT}（XML转换器）</li>
	 * </ul>
	 * 
	 * @return 系统默认的{@code MediaTypeConverterRegistry}实例，全局唯一
	 */
	public static MediaTypeConverterRegistry system() {
		if (system == null) {
			synchronized (MediaTypeConverterRegistry.class) {
				if (system == null) {
					system = new MediaTypeConverterRegistry();
					system.register(MediaType.APPLICATION_JSON, JsonFormat.DEFAULT);
					system.register(MediaType.APPLICATION_XML, XmlFormat.DEFAULT);
				}
			}
		}
		return system;
	}

	public MediaTypeConverterRegistry() {
		super(MediaType.SPECIFICITY_COMPARATOR);
	}

	/**
	 * 根据媒体类型获取匹配的转换器，支持兼容类型查找。
	 * <p>
	 * 查找逻辑：
	 * <ol>
	 * <li>优先查找精确匹配的媒体类型（如application/json）</li>
	 * <li>若未找到，遍历注册表查找兼容的媒体类型（通过{@link MediaType#isCompatibleWith(MediaType)}判断，
	 * 如application/*+json兼容application/json）</li>
	 * <li>若仍未找到，返回null</li>
	 * </ol>
	 * 
	 * @param mediaType 目标媒体类型（不可为null）
	 * @return 匹配的转换器；若未找到则返回null
	 */
	@Override
	public Converter getConverter(@NonNull MediaType mediaType) {
		Converter converter = getValues(mediaType).first();
		if (converter != null) {
			return converter;
		}
		for (Entry<MediaType, Converter> entry : getContainer().entrySet()) {
			if (mediaType.isCompatibleWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}
}