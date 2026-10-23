package run.soeasy.framework.web;

import java.util.function.Function;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;

/**
 * 响应实体的映射包装类，通过函数映射将源响应体类型转换为目标类型
 * 
 * <p>该类实现了{@link HttpResponseEntity}接口，采用装饰器模式包装另一个{@link HttpResponseEntity}实例，
 * 主要功能是通过提供的映射函数{@code mapper}将源响应体类型{@code S}转换为目标类型{@code T}，
 * 同时保留源响应实体的其他属性（响应头、状态码等）不变。</p>
 * 
 * <p>适用于需要在不修改原始响应结构的前提下，仅对响应体进行类型转换的场景，如API响应适配、数据格式转换等。</p>
 * 
 * @param <S> 源响应体类型（被转换的类型）
 * @param <T> 目标响应体类型（转换后的类型）
 */
@RequiredArgsConstructor
class MappedHttpResponseEntity<S, T> implements HttpResponseEntity<T> {

	/**
	 * 被包装的源响应实体，提供原始响应数据（响应头、状态码、源响应体等）
	 */
	protected final HttpResponseEntity<S> httpResponseEntity;

	/**
	 * 响应体转换函数，将源类型{@code S}映射为目标类型{@code T}
	 */
	private final Function<? super S, ? extends T> mapper;

	/**
	 * 获取转换后的响应体
	 * 
	 * <p>先从源响应实体中获取源响应体{@code S}，再通过映射函数{@code mapper}转换为目标类型{@code T}。
	 * 若源响应体为{@code null}，则直接将{@code null}传入映射函数（具体处理逻辑由函数实现决定）。</p>
	 * 
	 * @return 转换后的响应体（类型为{@code T}），可能为{@code null}
	 */
	@Override
	public T getBody() {
		S source = httpResponseEntity.getBody();
		return mapper.apply(source);
	}

	/**
	 * 判断响应是否包含非空响应体
	 * 
	 * <p>直接委托给源响应实体的{@link HttpResponseEntity#hasBody()}方法，
	 * 响应体的存在性判断基于原始响应体，与转换后的结果无关。</p>
	 * 
	 * @return 若源响应体存在则返回{@code true}，否则返回{@code false}
	 */
	@Override
	public boolean hasBody() {
		return httpResponseEntity.hasBody();
	}

	/**
	 * 获取HTTP响应头
	 * 
	 * <p>直接委托给源响应实体的{@link HttpResponseEntity#getHeaders()}方法，
	 * 保留原始响应的所有头信息，不做任何修改。</p>
	 * 
	 * @return 源响应实体的{@link HttpHeaders}对象
	 */
	@Override
	public HttpHeaders getHeaders() {
		return httpResponseEntity.getHeaders();
	}

	/**
	 * 获取HTTP状态码（枚举形式）
	 * 
	 * <p>直接委托给源响应实体的{@link HttpResponseEntity#getStatusCode()}方法，
	 * 保留原始响应的状态码信息。</p>
	 * 
	 * @return 源响应实体的{@link HttpStatus}枚举
	 */
	@Override
	public HttpStatus getStatusCode() {
		return httpResponseEntity.getStatusCode();
	}

	/**
	 * 获取HTTP状态码的数值形式
	 * 
	 * <p>直接委托给源响应实体的{@link HttpResponseEntity#getStatusCodeValue()}方法，
	 * 返回原始响应状态码的整数表示。</p>
	 * 
	 * @return 状态码整数（如200、404等）
	 */
	@Override
	public int getStatusCodeValue() {
		return httpResponseEntity.getStatusCodeValue();
	}

	/**
	 * 重写toString方法，返回包含状态码、响应体、响应头的字符串表示
	 * 
	 * <p>格式为：&lt;状态码 原因短语, 响应体, 响应头&gt;，其中响应体为转换后的{@code T}类型实例。
	 * 若响应体不存在，则省略响应体部分。</p>
	 * 
	 * @return 包含响应关键信息的字符串
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("<");
		HttpStatus httpStatus = getStatusCode();
		builder.append(getStatusCode());
		if (httpStatus != null) {
			builder.append(' ');
			builder.append(httpStatus.getReasonPhrase());
		}
		builder.append(',');
		T body = hasBody() ? getBody() : null;
		HttpHeaders headers = getHeaders();
		if (body != null) {
			builder.append(body);
			builder.append(',');
		}
		builder.append(headers);
		builder.append('>');
		return builder.toString();
	}
}