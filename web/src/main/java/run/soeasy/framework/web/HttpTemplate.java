package run.soeasy.framework.web;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.SSLContext;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import run.soeasy.framework.core.RandomUtils;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.Converter;

/**
 * 增强型HTTP客户端实现类，继承Spring
 * RestTemplate并实现HttpClientExecutor接口，整合类型安全请求处理与灵活的数据转换能力。
 * 
 * <p>
 * 核心特性：
 * <ul>
 * <li>内置媒体类型转换器注册表：通过{@link #mediaTypeConverterRegistry}管理自定义转换器，支持灵活扩展数据格式处理</li>
 * <li>类型安全响应处理：基于{@link ParameterizedTypeReference}解决泛型类型擦除问题，支持复杂类型（如List&amp;lt;T&amp;gt;）自动转换</li>
 * <li>SSL/TLS配置：通过{@link #setSSLContext(SSLContext)}自定义安全上下文，适配HTTPS场景</li>
 * <li>兼容HttpClientExecutor契约：实现标准化HTTP操作接口，与框架其他组件无缝协作</li>
 * </ul>
 * 
 * <p>
 * 转换器查找逻辑：调用{@link #getConverter(MediaType)}时，优先从当前实例的{@link #mediaTypeConverterRegistry}查找；
 * 若未找到，则委托给{@link HttpRequestExecutor}接口的默认实现（系统级注册表），兼顾自定义需求与系统默认配置。
 * 
 * <p>
 * 典型用法：
 * 
 * <pre>
 * // 创建客户端并注册自定义转换器
 * HttpTemplate httpTemplate = new HttpTemplate();
 * httpTemplate.getMediaTypeConverterRegistry().register(MediaType.TEXT_PLAIN, new StringConverter());
 * 
 * // 执行带泛型响应的请求
 * RequestEntity&lt;Void&gt; request = RequestEntity.get("/api/users").build();
 * ResponseEntity&lt;List&lt;User&gt;&gt; response = httpTemplate.doRequest(request, new TypeToken&lt;List&lt;User&gt;&gt;() {
 * }.getType());
 * </pre>
 * 
 * @see RestTemplate
 * @see HttpRequestExecutor
 * @see MediaTypeConverterRegistry
 * @see ParameterizedTypeReference
 */
@Getter
@Setter
@Slf4j
public class HttpTemplate extends RestTemplate implements HttpRequestExecutor {
	private String host;

	/**
	 * 媒体类型转换器注册表，用于管理当前实例的{@link MediaType}与{@link Converter}映射关系。
	 * <p>
	 * 支持注册自定义转换器，优先级高于系统级注册表，适用于当前实例的个性化数据转换需求。
	 */
	private final MediaTypeConverterRegistry mediaTypeConverterRegistry = new MediaTypeConverterRegistry();

	/**
	 * 执行HTTP请求并返回类型安全的响应实体，解决泛型类型擦除问题。
	 * <p>
	 * 通过{@link ParameterizedTypeReference}包装响应类型，保留泛型信息，
	 * 底层调用{@link RestTemplate#exchange(RequestEntity, ParameterizedTypeReference)}实现请求发送与响应转换。
	 * 
	 * @param <S>           请求体类型
	 * @param <T>           响应体类型（支持泛型）
	 * @param requestEntity 封装完整请求信息的实体（包含URL、方法、头信息、请求体）
	 * @param responseType  响应数据的目标类型（如new
	 *                      TypeToken&lt;List&lt;User&gt;&gt;(){}.getType()）
	 * @return 包含响应状态码、头信息和转换后响应体的ResponseEntity
	 * @throws WebException 当请求执行失败（如网络异常、转换错误）时抛出
	 */
	@Override
	public <S, T> ResponseEntity<T> doRequest(RequestEntity<S> requestEntity, Type responseType) throws WebException {
		String traceId = RandomUtils.uuid();
		log.trace("<{}> request {}", traceId, requestEntity);
		ResponseEntity<T> response = exchange(requestEntity, new ParameterizedTypeReference<T>() {
			@Override
			public Type getType() {
				return responseType;
			}
		});
		log.trace("<{}> response {}", traceId, response);
		return response;
	}

	/**
	 * 若{@link #host}不为空且请求URI为相对路径，自动拼接为绝对路径；
	 */
	@Override
	public <S, T> HttpResponseEntity<T> doRequest(@NonNull String uri, @NonNull HttpMethod httpMethod,
			@NonNull Type responseType, Object params, HttpHeaders httpHeaders, Object body, Class<S> bodyClass) {
		if (StringUtils.isNotEmpty(host)) {
			if (StringUtils.isEmpty(uri)) {
				uri = host;
			} else {
				URI requestUri;
				try {
					requestUri = new URI(uri);
					if (StringUtils.isEmpty(requestUri.getHost())) {
						uri = org.springframework.util.StringUtils.cleanPath(host + uri);
					}
				} catch (URISyntaxException e) {
					uri = org.springframework.util.StringUtils.cleanPath(host + uri);
				}
			}
		}
		return HttpRequestExecutor.super.doRequest(uri, httpMethod, responseType, params, httpHeaders, body, bodyClass);
	}

	/**
	 * 获取指定媒体类型对应的转换器，优先查找当前实例注册表，未找到则委托给接口默认实现。
	 * <p>
	 * 查找顺序：
	 * <ol>
	 * <li>从{@link #mediaTypeConverterRegistry}中查找匹配的转换器</li>
	 * <li>若未找到，调用{@link HttpRequestExecutor#getConverter(MediaType)}（系统级注册表）</li>
	 * </ol>
	 * 
	 * @param mediaType 目标媒体类型（不可为null）
	 * @return 匹配的转换器；若未找到则返回null
	 */
	@Override
	public Converter getConverter(@NonNull MediaType mediaType) {
		Converter converter = mediaTypeConverterRegistry.getConverter(mediaType);
		if (converter != null) {
			return converter;
		}
		return HttpRequestExecutor.super.getConverter(mediaType);
	}

	/**
	 * 设置SSL上下文，配置HTTPS通信的安全参数。
	 * <p>
	 * 通过创建{@link SimpleClientHttpsRequestFactory}替换默认请求工厂，将SSL上下文应用于HTTP客户端，
	 * 支持自定义证书、加密套件等安全配置。当{@code sslContext}为null时，使用默认SSL配置。
	 * 
	 * @param sslContext 包含安全配置的SSL上下文（可为null）
	 * @throws WebException 当请求工厂配置失败时抛出
	 */
	@Override
	public void setSSLContext(SSLContext sslContext) throws WebException {
		SimpleClientHttpsRequestFactory httpsRequestFactory = new SimpleClientHttpsRequestFactory();
		httpsRequestFactory.setSslSocketFactory(sslContext == null ? null : sslContext.getSocketFactory());
		setRequestFactory(httpsRequestFactory);
	}
}