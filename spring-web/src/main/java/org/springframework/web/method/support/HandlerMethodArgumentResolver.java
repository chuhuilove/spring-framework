/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.web.method.support;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * 策略接口,用于在给定请求的上下文中将方法参数解析为参数值.
 *
 * @author Arjen Poutsma
 * @see HandlerMethodReturnValueHandler
 * @since 3.1
 */
public interface HandlerMethodArgumentResolver {

	/**
	 * Whether the given {@linkplain MethodParameter method parameter} is
	 * supported by this resolver.
	 *
	 * @param parameter the method parameter to check
	 * @return {@code true} if this resolver supports the supplied parameter;
	 * {@code false} otherwise
	 */
	boolean supportsParameter(MethodParameter parameter);

	/**
	 * 从给定的请求中将方法参数解析为参数值.{@link ModelAndViewContainer}为请求提供对模型的访问.
	 * {@link WebDataBinderFactory}提供了在数据绑定和类型转换需要时创建WebDataBinder实例的方法.
	 *
	 * @param parameter     要解析的方法参数.这个参数必须之前已经传递给{@link #supportsParameter},并且其验证结果{@code true}.
	 *
	 * @param mavContainer  当前请求的ModelAndViewContainer
	 * @param webRequest    当前请求
	 * @param binderFactory 创建{@link WebDataBinder}实例的工厂
	 * @return 解析过的参数值,如果没有解析,返回{@code null}
	 * @throws Exception in case of errors with the preparation of argument values
	 */
	@Nullable
	Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
						   NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception;

}
