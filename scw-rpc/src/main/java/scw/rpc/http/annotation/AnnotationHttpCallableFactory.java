package scw.rpc.http.annotation;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import scw.core.Constants;
import scw.core.parameter.OverrideParameterDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.http.HttpHeaders;
import scw.http.HttpMethod;
import scw.http.MediaType;
import scw.http.client.HttpConnection;
import scw.http.client.HttpConnectionFactory;
import scw.net.uri.UriComponents;
import scw.net.uri.UriComponentsBuilder;
import scw.rpc.http.HttpConnectionCallableFactory;
import scw.value.StringValue;

public class AnnotationHttpCallableFactory extends HttpConnectionCallableFactory {
	private static final Pattern REQUEST_LINE_PATTERN = Pattern
			.compile("^([A-Z]+)[ ]*(.*)$");
	private final HttpConnectionFactory httpConnectionFactory;
	private final String host;
	private final HttpMethod defaultHttpMethod;
	private final MediaType mediaType;

	public AnnotationHttpCallableFactory(HttpConnectionFactory connectionFactory,
			HttpRemote host) {
		this(connectionFactory, host.value(), host.method(), new MediaType(MediaType.valueOf(host.contentType()), host.charsetName()));
	}
	
	public AnnotationHttpCallableFactory(HttpConnectionFactory connectionFactory, Path path){
		this(connectionFactory, path.value(), HttpMethod.GET, MediaType.APPLICATION_FORM_URLENCODED);
	}

	public AnnotationHttpCallableFactory(
			HttpConnectionFactory httpConnectionFactory, String host,
			HttpMethod defaultHttpMethod, MediaType mediaType) {
		this.httpConnectionFactory = httpConnectionFactory;
		this.host = host;
		this.defaultHttpMethod = defaultHttpMethod;
		this.mediaType = mediaType;
	}

	@Override
	protected HttpConnection getConnection(HttpHeaders httpHeaders, Class<?> clazz, Method method,
			Object[] args) {
		String url = null;
		HttpMethod httpMethod = null;
		RequestLine requestLine = method.getAnnotation(RequestLine.class);
		if(requestLine != null){
			Matcher requestLineMatcher = REQUEST_LINE_PATTERN.matcher(requestLine
					.value());
			if(requestLineMatcher.groupCount() == 1){
				url = StringUtils.cleanPath(host + requestLineMatcher.group(1));
			}else{
				/**
				 * POST /XX/XX
				 */
				url = StringUtils.cleanPath(host + requestLineMatcher.group(2));
				httpMethod = HttpMethod.valueOf(requestLineMatcher.group(1));
			}
		}
		
		//以下内容属于jsr内容
		Path path = method.getAnnotation(Path.class);
		if(path != null && url == null){
			url = StringUtils.cleanPath(host + path);
		}
		
		if(url == null){
			url = host;
		}
		
		if(httpMethod == null){
			Annotation[] annotations = method.getAnnotations();
			if(!ArrayUtils.isEmpty(annotations)){
				//因为在jsr311中各个请求方法上都注解了@HttpMethod,所以可以使用此方法遍历查找
				for(Annotation annotation : annotations){
					javax.ws.rs.HttpMethod requestMethod = annotation.annotationType().getAnnotation(javax.ws.rs.HttpMethod.class);
					if(requestMethod != null){
						httpMethod = HttpMethod.valueOf(requestMethod.value());
						break;
					}
				}
			}
		}
		
		if(httpMethod == null){
			httpMethod = defaultHttpMethod;
		}
		
		HttpHeaders requestHeaders = new HttpHeaders();
		if(httpHeaders != null){
			requestHeaders.putAll(httpHeaders);
		}
		
		Headers headers = clazz.getAnnotation(Headers.class);
		if(headers != null){
			requestHeaders.addHeaders(headers.value());
		}
		
		headers = method.getAnnotation(Headers.class);
		if(headers != null){
			requestHeaders.addHeaders(headers.value());
		}
		
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url);
		Map<String, Object> pathParameters = new LinkedHashMap<String, Object>();
		ParameterDescriptor[] parameterDescriptors = ParameterUtils.getParameterDescriptors(method);
		Map<String, Object> formParams = new LinkedHashMap<String, Object>();
		if(!ArrayUtils.isEmpty(parameterDescriptors)){
			for(int i=0; i<parameterDescriptors.length; i++){
				Object value = args[i];
				ParameterDescriptor parameterDescriptor = parameterDescriptors[i];
				DefaultValue defaultValue = parameterDescriptor.getAnnotatedElement().getAnnotation(DefaultValue.class);
				if(defaultValue != null){
					parameterDescriptor = new OverrideParameterDescriptor(parameterDescriptor, new StringValue(defaultValue.value()));
				}
				
				if(value == null && parameterDescriptor.getDefaultValue() != null){
					value = parameterDescriptor.getDefaultValue().getAsString();
				}
				
				HeaderParam headerParam = parameterDescriptor.getAnnotatedElement().getAnnotation(HeaderParam.class);
				if(headerParam != null){
					requestHeaders.add(headerParam.value(), String.valueOf(value));
					continue;
				}
				
				PathParam pathParam = parameterDescriptor.getAnnotatedElement().getAnnotation(PathParam.class);
				if(pathParam != null){
					pathParameters.put(pathParam.value(), value);
					continue;
				}
				
				QueryParam queryParam = parameterDescriptor.getAnnotatedElement().getAnnotation(QueryParam.class);
				if(queryParam != null){
					uriBuilder.queryParam(queryParam.value(), value);
					continue;
				}
				
				FormParam formParam = parameterDescriptor.getAnnotatedElement().getAnnotation(FormParam.class);
				if(formParam != null){
					formParams.put(formParam.value(), value);
					continue;
				}
				
				CookieParam cookieParam = parameterDescriptor.getAnnotatedElement().getAnnotation(CookieParam.class);
				if(cookieParam != null){
					requestHeaders.add(cookieParam.value(), String.valueOf(value));
					continue;
				}
				
				formParams.put(parameterDescriptor.getName(), value);
			}
		}
		
		//开始构造connection
		UriComponents uriComponents = uriBuilder.buildAndExpand(pathParameters);
		MediaType mediaType = requestHeaders.getContentType();
		if(mediaType == null){
			mediaType = this.mediaType;
		}
		
		String charsetName = mediaType.getCharsetName();
		if(charsetName == null){
			charsetName = Constants.UTF_8_NAME;
		}
		try {
			uriComponents = uriComponents.encode(charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(charsetName, e);
		}

		HttpConnection httpConnection = httpConnectionFactory.createConnection(httpMethod, uriComponents.toUri());
		if(!formParams.isEmpty()){
			httpConnection = httpConnection.body(formParams);
		}
		httpConnection = httpConnection.contentType(mediaType);
		return httpConnection;
	}
}
