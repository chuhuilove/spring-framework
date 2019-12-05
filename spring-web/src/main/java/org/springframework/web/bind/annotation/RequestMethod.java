/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.web.bind.annotation;

/**
 * Java 5 enumeration of HTTP request methods. Intended for use with the
 * {@link RequestMapping#method()} attribute of the {@link RequestMapping} annotation.
 *
 * <p>注意,默认情况下,{@link org.springframework.web.servlet.DispatcherServlet}只
 * 支持 GET, HEAD, POST, PUT, PATCH and DELETE .
 * DispatcherServlet将使用默认的HttpServlet行为处理TRACE和OPTIONS,除非明确告知也要分派这些请求类型:
 * 检出"dispatchOptionsRequest"和"dispatchTraceRequest"属性,并在必要时将其切换为"true".
 *
 * DispatcherServlet will
 * process TRACE and OPTIONS with the default HttpServlet behavior unless explicitly
 * told to dispatch those request types as well: Check out the "dispatchOptionsRequest"
 * and "dispatchTraceRequest" properties, switching them to "true" if necessary.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see RequestMapping
 * @see org.springframework.web.servlet.DispatcherServlet#setDispatchOptionsRequest
 * @see org.springframework.web.servlet.DispatcherServlet#setDispatchTraceRequest
 */
public enum RequestMethod {

	GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE

}
