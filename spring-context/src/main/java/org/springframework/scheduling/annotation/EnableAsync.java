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

package org.springframework.scheduling.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

/**
 * 启用Spring的异步方法执行功能，类似于Spring的{@code <task:*>} XML名称空间中的功能.
 * <p>与@{@link Configuration Configuration}类一起使用,如下所示,为整个Spring容器启用基于注解的异步处理:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAsync
 * public class AppConfig {
 *
 * }</pre>
 * <p>
 * MyAsyncBean是用户定义的类型，具有一个或多个方法，这些方法使用Spring的@Async批注，EJB 3.1 @ javax.ejb.Asynchronous批注或通过comment（）属性指定的任何自定义批注进行批注。
 * {@code MyAsyncBean}是用户定义的类型,带有一个或多个方法,
 * 这些方法要么使用Spring的{@code @Async}注解,要么使用EJB 3.1的{@code @javax.ejb.Asynchronous}注解,
 * 要么使用{@link #annotation}属性指定的任何自定义注解.
 * 切面是透明地添加到任何注册的bean,例如通过这个配置:
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AnotherAppConfig {
 *
 *     &#064;Bean
 *     public MyAsyncBean asyncBean() {
 *         return new MyAsyncBean();
 *     }
 * }</pre>
 *
 * <p>默认情况下,Spring将搜索关联的线程池定义:要么是上下文中唯一的{@link org.springframework.core.task.TaskExecutor} bean,
 * 要么是名为"taskExecutor"的{@link java.util.concurrent.Executor} bean.
 * 如果两者都不可解析,则{@link org.springframework.core.task.SimpleAsyncTaskExecutor}将用于处理异步方法调用.
 * 此外,具有{@code void}返回类型的带注解的方法无法将任何异常发送回调用方.
 * 默认情况下,这种未捕获的异常只会被记录下来.
 *
 * <p>要定制所有实现,请实现{@link AsyncConfigurer} 并提供:
 * <ul>
 * <li>通过{@link AsyncConfigurer#getAsyncExecutor getAsyncExecutor()}方法创建自己的{@link java.util.concurrent.Executor Executor},和</li>
 * <li>通过{@link AsyncConfigurer#getAsyncUncaughtExceptionHandler
 * getAsyncUncaughtExceptionHandler()}方法创建自己的{@link org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
 * AsyncUncaughtExceptionHandler}.
 *
 * </li>
 * </ul>
 *
 * <p><b>注意:{@link AsyncConfigurer}配置类在应用程序上下文引导中提前初始化.
 * 如果需要依赖于其他bean,请确保尽可能地将它们声明为'lazy',以便让它们也通过其他后置处理程序.</b>
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAsync
 * public class AppConfig implements AsyncConfigurer {
 *
 *     &#064;Override
 *     public Executor getAsyncExecutor() {
 *         ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
 *         executor.setCorePoolSize(7);
 *         executor.setMaxPoolSize(42);
 *         executor.setQueueCapacity(11);
 *         executor.setThreadNamePrefix("MyExecutor-");
 *         executor.initialize();
 *         return executor;
 *     }
 *
 *     &#064;Override
 *     public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
 *         return new MyAsyncUncaughtExceptionHandler();
 *     }
 * }</pre>
 *
 * <p>如果仅需要自定义一项,则可以返回{@code null}以保留默认设置.
 * 考虑尽可能从{@link AsyncConfigurerSupport}进行扩展.
 *
 * <p>注意:在上面的示例中,{@code ThreadPoolTaskExecutor}不是完全托管的Spring bean.
 * 如果要使用完全托管的bean,请将{@code @Bean}注解添加到{@code getAsyncExecutor()}方法中.
 * 在这种情况下,不再需要手动调用{@code executor.initialize()} 方法,因为在初始化Bean时将自动调用该方法.
 *
 * <p>作为参考,可以将上面的示例与以下Spring XML配置进行比较:
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;task:annotation-driven executor="myExecutor" exception-handler="exceptionHandler"/&gt;
 *
 *     &lt;task:executor id="myExecutor" pool-size="7-42" queue-capacity="11"/&gt;
 *
 *     &lt;bean id="asyncBean" class="com.foo.MyAsyncBean"/&gt;
 *
 *     &lt;bean id="exceptionHandler" class="com.foo.MyAsyncUncaughtExceptionHandler"/&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 * <p>除了基{@code Executor}的<em>线程名前缀</em>的设置之外,
 * 上述基于XML和基于JavaConfig的示例是等效的.
 * 这是因为{@code <task:executor>}元素没有公开这样的属性.
 * 这演示了基于javaconfig的方法如何通过直接访问实际组件来实现最大的可配置性。
 *
 * <p>{@link #mode} 属性控制如何应用通知:如果模式是{@link AdviceMode#PROXY} (默认),
 * 那么其他属性控制代理的行为.请注意,代理模式只允许通过代理拦截调用;同一类内的本地调用不能通过这种方式被截获.
 *
 * <p>注意,如果{@linkplain #mode}被设置为{@link AdviceMode#ASPECTJ},那么{@link #proxyTargetClass}属性的值将被忽略.
 * 还要注意,在这种情况下,{@code spring-aspects}模块JAR必须存在于类路径中,并使用编译时织入或加载时织入将切面应用于受影响的类.
 * 在这种情况下不涉及任何代理;本地调用也会被拦截.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @author Sam Brannen
 * @see Async
 * @see AsyncConfigurer
 * @see AsyncConfigurationSelector
 * @since 3.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AsyncConfigurationSelector.class)
public @interface EnableAsync {

	/**
	 * Indicate the 'async' annotation type to be detected at either class
	 * or method level.
	 * <p>By default, both Spring's @{@link Async} annotation and the EJB 3.1
	 * {@code @javax.ejb.Asynchronous} annotation will be detected.
	 * <p>This attribute exists so that developers can provide their own
	 * custom annotation type to indicate that a method (or all methods of
	 * a given class) should be invoked asynchronously.
	 */
	Class<? extends Annotation> annotation() default Annotation.class;

	/**
	 * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
	 * to standard Java interface-based proxies.
	 * <p><strong>Applicable only if the {@link #mode} is set to {@link AdviceMode#PROXY}</strong>.
	 * <p>The default is {@code false}.
	 * <p>Note that setting this attribute to {@code true} will affect <em>all</em>
	 * Spring-managed beans requiring proxying, not just those marked with {@code @Async}.
	 * For example, other beans marked with Spring's {@code @Transactional} annotation
	 * will be upgraded to subclass proxying at the same time. This approach has no
	 * negative impact in practice unless one is explicitly expecting one type of proxy
	 * vs. another &mdash; for example, in tests.
	 */
	boolean proxyTargetClass() default false;

	/**
	 * Indicate how async advice should be applied.
	 * <p><b>The default is {@link AdviceMode#PROXY}.</b>
	 * Please note that proxy mode allows for interception of calls through the proxy
	 * only. Local calls within the same class cannot get intercepted that way; an
	 * {@link Async} annotation on such a method within a local call will be ignored
	 * since Spring's interceptor does not even kick in for such a runtime scenario.
	 * For a more advanced mode of interception, consider switching this to
	 * {@link AdviceMode#ASPECTJ}.
	 */
	AdviceMode mode() default AdviceMode.PROXY;

	/**
	 * Indicate the order in which the {@link AsyncAnnotationBeanPostProcessor}
	 * should be applied.
	 * <p>The default is {@link Ordered#LOWEST_PRECEDENCE} in order to run
	 * after all other post-processors, so that it can add an advisor to
	 * existing proxies rather than double-proxy.
	 */
	int order() default Ordered.LOWEST_PRECEDENCE;

}
