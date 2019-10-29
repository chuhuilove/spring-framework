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

package org.springframework.web.servlet.config.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;

/**
 *
 * 资源处理注册器
 *
 * Stores registrations of resource handlers for serving static resources such as images, css files and others
 * through Spring MVC including setting cache headers optimized for efficient loading in a web browser.
 * Resources can be served out of locations under web application root, from the classpath, and others.
 *
 * <p>To create a resource handler, use {@link #addResourceHandler(String...)} providing the URL path patterns
 * for which the handler should be invoked to serve static resources (e.g. {@code "/resources/**"}).
 *
 * <p>Then use additional methods on the returned {@link ResourceHandlerRegistration} to add one or more
 * locations from which to serve static content from (e.g. {{@code "/"},
 * {@code "classpath:/META-INF/public-web-resources/"}}) or to specify a cache period for served resources.
 *
 *
 * 通过Spring MVC存储为静态资源(如图像、css文件等)提供服务的资源处理程序的注册,
 * 包括设置为在web浏览器中有效加载而优化的缓存头文件.
 *
 * 可以从web应用程序根目录下的位置、类路径和其他位置提供资源.
 *
 * 创建资源处理器,使用 {@link #addResourceHandler(String...)}提供URL路径模式,
 * 应调用该URL路径处理器以提供静态资源(e.g. {@code "/resources/**"}).
 *
 * 然后在返回的{@link ResourceHandlerRegistration}上使用其他方法来添加一个或多个位置,
 * 以从中提供静态内容(e.g. {{@code "/"},{@code "classpath:/META-INF/public-web-resources/"}})
 * 或为所资源指定缓存期.
 *
 *
 * 这个类最主要的功能就是设置静态资源映射,在{@link WebMvcConfigurationSupport#resourceHandlerMapping()} 中创建
 * 然后由程序员去实现添加静态资源.
 * 本类除了构造方法外,{@link #hasMappingForPattern(String)}用来进行匹配之外,
 * {@link #addResourceHandler(String...)}用来添加映射资源,其返回的{@link ResourceHandlerRegistration},用来表示该资源的属性,
 * 如具体的路径,资源的缓存周期等.
 *
 * 比如,一个js文件,在调用{@link #addResourceHandler(String...)}后,再设置{@link ResourceHandlerRegistration#addResourceLocations}来表示
 * 该js的具体路径.
 *
 *
 * 如:
 *      	registry.addResourceHandler("images/**").addResourceLocations("/images/");
 *         registry.addResourceHandler("css/**").addResourceLocations("/css/");
 *         registry.addResourceHandler("js/**").addResourceLocations("/js/");
 *         registry.addResourceHandler("fonts/**").addResourceLocations("/fonts/");
 *         registry.addResourceHandler("layui/**").addResourceLocations("/layui/");
 *         registry.addResourceHandler("webjars/**").addResourceLocations("/webjars/");
 *         registry.addResourceHandler("*.html").addResourceLocations("/WEB-INF/chathtml/");
 *
 *	将静态资源放过去,不进行拦截
 *
 *
 *
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see DefaultServletHandlerConfigurer
 */
public class ResourceHandlerRegistry {

	private final ServletContext servletContext;

	private final ApplicationContext applicationContext;

	@Nullable
	private final ContentNegotiationManager contentNegotiationManager;

	@Nullable
	private final UrlPathHelper pathHelper;

	private final List<ResourceHandlerRegistration> registrations = new ArrayList<>();

	private int order = Ordered.LOWEST_PRECEDENCE - 1;


	/**
	 * Create a new resource handler registry for the given application context.
	 * @param applicationContext the Spring application context
	 * @param servletContext the corresponding Servlet context
	 */
	public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext) {
		this(applicationContext, servletContext, null);
	}

	/**
	 * Create a new resource handler registry for the given application context.
	 * @param applicationContext the Spring application context
	 * @param servletContext the corresponding Servlet context
	 * @param contentNegotiationManager the content negotiation manager to use
	 * @since 4.3
	 */
	public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext,
			@Nullable ContentNegotiationManager contentNegotiationManager) {

		this(applicationContext, servletContext, contentNegotiationManager, null);
	}

	/**
	 * A variant of
	 * {@link #ResourceHandlerRegistry(ApplicationContext, ServletContext, ContentNegotiationManager)}
	 * that also accepts the {@link UrlPathHelper} used for mapping requests to static resources.
	 * @since 4.3.13
	 */
	public ResourceHandlerRegistry(ApplicationContext applicationContext, ServletContext servletContext,
			@Nullable ContentNegotiationManager contentNegotiationManager, @Nullable UrlPathHelper pathHelper) {

		Assert.notNull(applicationContext, "ApplicationContext is required");
		this.applicationContext = applicationContext;
		this.servletContext = servletContext;
		this.contentNegotiationManager = contentNegotiationManager;
		this.pathHelper = pathHelper;
	}


	/**
	 *
	 * 添加一个资源处理器,用于根据指定的URL路径模式提供静态资源.
	 * 处理器将为每个与指定路径模式之一匹配的传入请求调用.
	 *
	 *
	 * Add a resource handler for serving static resources based on the specified URL path patterns.
	 * The handler will be invoked for every incoming request that matches to one of the specified
	 * path patterns.
	 *
	 * 类似于{@code "/static/**"}或{@code "/css/{filename:\\w+\\.css}"}这种模式都可以通过.
	 * 更详细的语义,请移步这里:{@link org.springframework.util.AntPathMatcher}
	 *
	 *
	 * @return a {@link ResourceHandlerRegistration} to use to further configure the
	 * registered resource handler
	 */
	public ResourceHandlerRegistration addResourceHandler(String... pathPatterns) {
		ResourceHandlerRegistration registration = new ResourceHandlerRegistration(pathPatterns);
		this.registrations.add(registration);
		return registration;
	}

	/**
	 * Whether a resource handler has already been registered for the given path pattern.
	 */
	public boolean hasMappingForPattern(String pathPattern) {
		for (ResourceHandlerRegistration registration : this.registrations) {
			if (Arrays.asList(registration.getPathPatterns()).contains(pathPattern)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Specify the order to use for resource handling relative to other {@link HandlerMapping HandlerMappings}
	 * configured in the Spring MVC application context.
	 * <p>The default value used is {@code Integer.MAX_VALUE-1}.
	 */
	public ResourceHandlerRegistry setOrder(int order) {
		this.order = order;
		return this;
	}

	/**
	 * Return a handler mapping with the mapped resource handlers; or {@code null} in case
	 * of no registrations.
	 */
	@Nullable
	protected AbstractHandlerMapping getHandlerMapping() {
		if (this.registrations.isEmpty()) {
			return null;
		}

		Map<String, HttpRequestHandler> urlMap = new LinkedHashMap<>();
		for (ResourceHandlerRegistration registration : this.registrations) {
			for (String pathPattern : registration.getPathPatterns()) {
				ResourceHttpRequestHandler handler = registration.getRequestHandler();
				if (this.pathHelper != null) {
					handler.setUrlPathHelper(this.pathHelper);
				}
				if (this.contentNegotiationManager != null) {
					handler.setContentNegotiationManager(this.contentNegotiationManager);
				}
				handler.setServletContext(this.servletContext);
				handler.setApplicationContext(this.applicationContext);
				try {
					handler.afterPropertiesSet();
				}
				catch (Throwable ex) {
					throw new BeanInitializationException("Failed to init ResourceHttpRequestHandler", ex);
				}
				urlMap.put(pathPattern, handler);
			}
		}

		// 返回一个对象
		/**
		 * 返回一个{@link SimpleUrlHandlerMapping}对象.
		 * 这个函数在{@link WebMvcConfigurationSupport#resourceHandlerMapping()}中调用.
		 * 这里只对Mapping对象设置了基本的属性,没有设置更多的拦截器.
		 * 拦截器在{@link WebMvcConfigurationSupport#resourceHandlerMapping()}中设置.
		 */
		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setOrder(this.order);
		handlerMapping.setUrlMap(urlMap);
		return handlerMapping;
	}

}
