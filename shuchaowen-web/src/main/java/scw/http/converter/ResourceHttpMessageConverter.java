/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.http.converter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import scw.http.HttpInputMessage;
import scw.http.HttpOutputMessage;
import scw.http.MediaType;
import scw.io.ByteArrayResource;
import scw.io.InputStreamResource;
import scw.io.Resource;
import scw.net.FileMimeTypeUitls;
import scw.net.MimeType;
import scw.util.StreamUtils;

public class ResourceHttpMessageConverter extends AbstractHttpMessageConverter<Resource> {

	public ResourceHttpMessageConverter() {
		super(MediaType.ALL);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return Resource.class.isAssignableFrom(clazz);
	}

	@Override
	protected Resource readInternal(Class<? extends Resource> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {

		if (InputStreamResource.class == clazz) {
			return new InputStreamResource(inputMessage.getBody());
		} else if (clazz.isAssignableFrom(ByteArrayResource.class)) {
			byte[] body = StreamUtils.copyToByteArray(inputMessage.getBody());
			return new ByteArrayResource(body);
		} else {
			throw new IllegalStateException("Unsupported resource class: " + clazz);
		}
	}

	@Override
	protected MediaType getDefaultContentType(Resource resource) {
		MimeType mimeType = FileMimeTypeUitls.getMimeType(resource);
		if (mimeType == null) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}
		return new MediaType(mimeType);
	}

	@Override
	protected Long getContentLength(Resource resource, MediaType contentType) throws IOException {
		// Don't try to determine contentLength on InputStreamResource - cannot
		// be read afterwards...
		// Note: custom InputStreamResource subclasses could provide a
		// pre-calculated content length!
		if (InputStreamResource.class == resource.getClass()) {
			return null;
		}
		long contentLength = resource.contentLength();
		return (contentLength < 0 ? null : contentLength);
	}

	@Override
	protected void writeInternal(Resource resource, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {

		writeContent(resource, outputMessage);
	}

	protected void writeContent(Resource resource, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		try {
			InputStream in = resource.getInputStream();
			try {
				StreamUtils.copy(in, outputMessage.getBody());
			} catch (NullPointerException ex) {
				// ignore, see SPR-13620
			} finally {
				try {
					in.close();
				} catch (Throwable ex) {
					// ignore, see SPR-12999
				}
			}
		} catch (FileNotFoundException ex) {
			// ignore, see SPR-12999
		}
	}

}
