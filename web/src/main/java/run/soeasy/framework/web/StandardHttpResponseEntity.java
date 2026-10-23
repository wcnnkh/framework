package run.soeasy.framework.web;

import java.lang.reflect.Type;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class StandardHttpResponseEntity<T> implements HttpResponseEntity<T> {
	private final ResponseEntity<T> responseEntity;
	private final MediaTypeConverterFactory mediaTypeConverterFactory;

	@Override
	public <R> HttpResponseEntity<R> cast(Class<R> bodyType) {
		return map(bodyType, mediaTypeConverterFactory);
	}

	@Override
	public <R> HttpResponseEntity<R> cast(Type bodyType) {
		return map(bodyType, mediaTypeConverterFactory);
	}

	@Override
	public HttpResponseEntity<T> assignableFrom(@NonNull MediaTypeConverterFactory mediaTypeConverterFactory) {
		return new StandardHttpResponseEntity<>(responseEntity, mediaTypeConverterFactory);
	}

	@Override
	public boolean hasBody() {
		return responseEntity.hasBody();
	}

	@Override
	public T getBody() {
		return responseEntity.getBody();
	}

	@Override
	public HttpHeaders getHeaders() {
		return responseEntity.getHeaders();
	}

	@Override
	public HttpStatus getStatusCode() {
		return responseEntity.getStatusCode();
	}

	@Override
	public int getStatusCodeValue() {
		return responseEntity.getStatusCodeValue();
	}

	@Override
	public ResponseEntity<T> shared() {
		return responseEntity;
	}

	@Override
	public String toString() {
		return responseEntity.toString();
	}

	@Override
	public int hashCode() {
		return responseEntity.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof StandardHttpResponseEntity) {
			return this.responseEntity.equals(((StandardHttpResponseEntity<?>) obj).responseEntity);
		}
		return this.responseEntity.equals(obj) || obj.equals(this.responseEntity);
	}
}
