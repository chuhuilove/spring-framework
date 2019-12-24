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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 启用spring的定时任务执行功能,类似于Spring的{@code <task:*>} XML名称空间中的功能.
 * 用于@{@link Configuration}类上:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * public class AppConfig {
 *
 *     // various &#064;Bean definitions
 * }</pre>
 * <p>
 * 这样可以在容器中的任何Spring管理的bean上检测@{@link Scheduled}注解.比如,给定一个类{@code MyTask}
 *
 * <pre class="code">
 * package com.myco.tasks;
 *
 * public class MyTask {
 *
 *     &#064;Scheduled(fixedRate=1000)
 *     public void work() {
 *         // 任务执行逻辑
 *     }
 * }</pre>
 * <p>
 * 以下配置将确保每1000毫秒调用一次{@code MyTask.work()}:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public MyTask task() {
 *         return new MyTask();
 *     }
 * }</pre>
 * <p>
 * 或者，如果使用{@code @Component}注解{@code MyTask},
 * 则以下配置将确保以所需的间隔调用其{@code @Scheduled}方法:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * &#064;ComponentScan(basePackages="com.myco.tasks")
 * public class AppConfig {
 * }</pre>
 * <p>
 * 带有{@code @Scheduled}注解的方法甚至可以直接在{@code @Configuration}类中声明:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * public class AppConfig {
 *
 *     &#064;Scheduled(fixedRate=1000)
 *     public void work() {
 *         // 任务执行逻辑
 *     }
 * }</pre>
 *
 * <p>默认情况下,将搜索关联的调度程序定义:要么是上下文中唯一的{@link org.springframework.scheduling.TaskScheduler} bean,
 * 要么是名为"taskScheduler"的{@code TaskScheduler} bean;
 * 对于{@link java.util.concurrent.ScheduledExecutorService} bean也将执行相同的查找.
 * 如果两者都不无法解决问题,则将创建一个本地单线程作为默认调度器并在注册中心内使用.
 *
 * <p>当注解不足以满足我们的需求时,{@code @Configuration}类可以实现{@link SchedulingConfigurer}.
 * 这允许访问底层{@link ScheduledTaskRegistrar}实例.
 * 例如,下面的例子演示了如何定制用来执行计划任务的{@link Executor}:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * public class AppConfig implements SchedulingConfigurer {
 *
 *     &#064;Override
 *     public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
 *         taskRegistrar.setScheduler(taskExecutor());
 *     }
 *
 *     &#064;Bean(destroyMethod="shutdown")
 *     public Executor taskExecutor() {
 *         return Executors.newScheduledThreadPool(100);
 *     }
 * }</pre>
 *
 * <p>注意，在上面的例子中使用了{@code @Bean(destroyMethod="shutdown")}.
 * 这确保了在Spring容器关闭时,任务执行器被正确关闭.
 *
 * <p>实现{@code SchedulingConfigurer}还可以通过{@code ScheduledTaskRegistrar}对任务注册进行细粒度的控制.
 * 例如,以下代码根据自定义{@code Trigger}实现配置特定bean方法的执行:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableScheduling
 * public class AppConfig implements SchedulingConfigurer {
 *
 *     &#064;Override
 *     public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
 *         taskRegistrar.setScheduler(taskScheduler());
 *         taskRegistrar.addTriggerTask(
 *             new Runnable() {
 *                 public void run() {
 *                     myTask().work();
 *                 }
 *             },
 *             new CustomTrigger()
 *         );
 *     }
 *
 *     &#064;Bean(destroyMethod="shutdown")
 *     public Executor taskScheduler() {
 *         return Executors.newScheduledThreadPool(42);
 *     }
 *
 *     &#064;Bean
 *     public MyTask myTask() {
 *         return new MyTask();
 *     }
 * }</pre>
 *
 * <p>作为参考,可以将上面的示例与以下Spring XML配置进行比较:
 * <pre class="code">
 * &lt;beans>
 *
 *     &lt;task:annotation-driven scheduler="taskScheduler"/&gt;
 *
 *     &lt;task:scheduler id="taskScheduler" pool-size="42"/&gt;
 *
 *     &lt;task:scheduled-tasks scheduler="taskScheduler"&gt;
 *         &lt;task:scheduled ref="myTask" method="work" fixed-rate="1000"/&gt;
 *     &lt;/task:scheduled-tasks&gt;
 *
 *     &lt;bean id="myTask" class="com.foo.MyTask"/&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 * <p>这些示例是等效的,只是在XML中使用<em>fixed-rate</em>而不是自定义的<em>{@code Trigger}</em>实现.
 * 这是因为任务：计划的名称空间无法轻松地提供这种支持.
 * The examples are equivalent save that in XML a <em>fixed-rate</em> period is used
 * instead of a custom <em>{@code Trigger}</em> implementation; this is because the
 * {@code task:} namespace {@code scheduled} cannot easily expose such support. This is
 * but one demonstration how the code-based approach allows for maximum configurability
 * through direct access to actual componentry.<p>
 *
 * <b>注意:{@code @EnableScheduling}只适用于它的本地应用程序上下文,允许在不同的级别选择性地调度bean.</b>
 * 如果需要在多个级别上应用其行为,请在每个单独的上下文中重新声明{@code @EnableScheduling},
 * 例如,公有root Web应用程序上下文以及任何单独的DispatcherServlet应用程序上下文.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @see Scheduled
 * @see SchedulingConfiguration
 * @see SchedulingConfigurer
 * @see ScheduledTaskRegistrar
 * @see Trigger
 * @see ScheduledAnnotationBeanPostProcessor
 * @since 3.1
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SchedulingConfiguration.class)
@Documented
public @interface EnableScheduling {

}
