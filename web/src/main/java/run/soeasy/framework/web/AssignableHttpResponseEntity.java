package run.soeasy.framework.web;

import java.lang.reflect.Type;
import java.util.function.Function;

import lombok.NonNull;

/**
 * 支持类型转换和转换器工厂切换的响应实体包装类
 * 
 * <p>该类继承自{@link MappedHttpResponseEntity}，且源类型与目标类型均为{@code T}（通过{@link Function#identity()}保持类型一致），
 * 主要扩展了两项核心能力：
 * <ul>
 * <li>通过{@link #cast(Class)}和{@link #cast(Type)}方法将响应体转换为指定目标类型</li>
 * <li>通过{@link #assignableFrom(MediaTypeConverterFactory)}方法切换用于转换的媒体类型转换器工厂</li>
 * </ul>
 * 适用于需要在响应处理链路中动态切换转换策略或执行类型转换的场景。
 * 
 * @param <T> 响应体的原始类型（源类型与目标类型一致）
 */
class AssignableHttpResponseEntity<T> extends MappedHttpResponseEntity<T, T> {

	/**
	 * 用于类型转换的媒体类型转换器工厂，提供基于媒体类型的转换器查找能力
	 */
	private final MediaTypeConverterFactory mediaTypeConverterFactory;

	/**
	 * 构造方法：创建AssignableHttpResponseEntity实例
	 * 
	 * @param httpResponseEntity        被包装的源响应实体，提供原始响应数据（不可为null）
	 * @param mediaTypeConverterFactory 用于类型转换的媒体类型转换器工厂（不可为null）
	 */
	public AssignableHttpResponseEntity(HttpResponseEntity<T> httpResponseEntity,
			MediaTypeConverterFactory mediaTypeConverterFactory) {
		// 调用父类构造，使用identity函数保持源类型与目标类型一致（不执行额外转换）
		super(httpResponseEntity, Function.identity());
		this.mediaTypeConverterFactory = mediaTypeConverterFactory;
	}

	/**
	 * 将响应体转换为指定的目标类类型
	 * 
	 * <p>通过当前{@code mediaTypeConverterFactory}获取合适的转换器，将原始响应体类型{@code T}转换为目标类型{@code R}。
	 * 本质是调用{@link MappedHttpResponseEntity#map(Type, MediaTypeConverterFactory)}方法实现转换。</p>
	 * 
	 * @param <R>      目标类型
	 * @param bodyType 目标类型的Class对象（不可为null）
	 * @return 包含转换后响应体的新{@link HttpResponseEntity}实例
	 */
	@Override
	public <R> HttpResponseEntity<R> cast(Class<R> bodyType) {
		return map(bodyType, mediaTypeConverterFactory);
	}

	/**
	 * 将响应体转换为指定的泛型类型（支持复杂类型如List&lt;T&gt;）
	 * 
	 * <p>与{@link #cast(Class)}类似，但支持通过{@link Type}描述泛型类型，解决泛型擦除导致的类型信息丢失问题。</p>
	 * 
	 * @param <R>      目标类型
	 * @param bodyType 目标类型的Type对象（如通过{@code new TypeToken<List<User>>(){}.getType()}获取，不可为null）
	 * @return 包含转换后响应体的新{@link HttpResponseEntity}实例
	 */
	@Override
	public <R> HttpResponseEntity<R> cast(Type bodyType) {
		return map(bodyType, mediaTypeConverterFactory);
	}

	/**
	 * 切换用于类型转换的媒体类型转换器工厂
	 * 
	 * <p>若传入的转换器工厂与当前工厂相同，则返回当前实例（避免不必要的对象创建）；
	 * 否则创建新的{@code AssignableHttpResponseEntity}实例，使用新的转换器工厂。</p>
	 * 
	 * @param mediaTypeConverterFactory 新的媒体类型转换器工厂（不可为null）
	 * @return 若工厂相同则返回当前实例，否则返回新实例
	 */
	@Override
	public HttpResponseEntity<T> assignableFrom(@NonNull MediaTypeConverterFactory mediaTypeConverterFactory) {
		if (mediaTypeConverterFactory == this.mediaTypeConverterFactory) {
			return this;
		}
		return new AssignableHttpResponseEntity<>(this.httpResponseEntity, mediaTypeConverterFactory);
	}
}