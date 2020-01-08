/*
 * Copyright 2002-2016 the original author or authors.
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

package scw.http.client.support;

import java.util.ArrayList;
import java.util.List;

import scw.core.utils.CollectionUtils;
import scw.http.client.AsyncClientHttpRequestFactory;
import scw.http.client.AsyncClientHttpRequestInterceptor;
import scw.http.client.InterceptingAsyncClientHttpRequestFactory;

/**
 * The HTTP accessor that extends the base {@link AsyncHttpAccessor} with
 * request intercepting functionality.
 *
 * @author Jakub Narloch
 * @author Rossen Stoyanchev
 * @since 4.3
 */
public abstract class InterceptingAsyncHttpAccessor extends AsyncHttpAccessor {

    private List<AsyncClientHttpRequestInterceptor> interceptors =
            new ArrayList<AsyncClientHttpRequestInterceptor>();


    /**
     * Set the request interceptors that this accessor should use.
     * @param interceptors the list of interceptors
     */
    public void setInterceptors(List<AsyncClientHttpRequestInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * Return the request interceptor that this accessor uses.
     */
    public List<AsyncClientHttpRequestInterceptor> getInterceptors() {
        return this.interceptors;
    }


    @Override
    public AsyncClientHttpRequestFactory getAsyncRequestFactory() {
        AsyncClientHttpRequestFactory delegate = super.getAsyncRequestFactory();
        if (!CollectionUtils.isEmpty(getInterceptors())) {
            return new InterceptingAsyncClientHttpRequestFactory(delegate, getInterceptors());
        }
        else {
            return delegate;
        }
    }

}
