/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.context;

/**
 * 回调接口,用于在{@linkplain ConfigurableApplicationContext#refresh() refreshed}
 * 之前初始化Spring {@link ConfigurableApplicationContext}.
 * <p>
 * 通常用于需要对应用程序上下文进行一些程序化初始化的web应用程序中.
 * 例如,注册属性来源(xml配置文件或注解)或针对{@linkplain ConfigurableApplicationContext#getEnvironment() 上下文环境}激活配置文件.
 * 请参见{@code ContextLoader}和{@code FrameworkServlet}支持,以分别查看"contextInitializerClasses"上下文参数和init参数的声明.
 *
 * <p>
 *  推荐{@code ApplicationContextInitializer}处理器检测是否已经实现了Spring的{@link org.springframework.core.Ordered Ordered}接口,
 *  或者是否存在@{@link org.springframework.core.annotation.Order Order}注解,
 *  如果存在,则在调用之前对实例进行相应的排序.
 *
 * @author Chris Beams
 * @since 3.1
 * @param <C> the application context type
 * @see org.springframework.web.context.ContextLoader#customizeContext
 * @see org.springframework.web.context.ContextLoader#CONTEXT_INITIALIZER_CLASSES_PARAM
 * @see org.springframework.web.servlet.FrameworkServlet#setContextInitializerClasses
 * @see org.springframework.web.servlet.FrameworkServlet#applyInitializers
 */
public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {

	/**
	 * 初始化给定的应用上下文.
	 * @param applicationContext the application to configure
	 */
	void initialize(C applicationContext);

}
