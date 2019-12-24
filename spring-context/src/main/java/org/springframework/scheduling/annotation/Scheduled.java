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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记要调度的方法的注解.必须指定{@link #cron()},{@link #fixedDelay()},或{@link #fixedRate()}这三个属性中的一个.
 *
 * <p>带注解的方法必须是无参的.且其返回值必须是{@code void}类型,否则通过调度器调用时将忽略返回的值.
 *
 * <p>@Scheduled注释的处理是通过注册{@link ScheduledAnnotationBeanPostProcessor}来执行的.
 * 这可以手动完成,或者更方便地通过{@code <task:annotation-driven/>}元素或@{@link EnableScheduling}注解完成.
 *
 * <p>此注解可以用作<em>元注解</em>,以创建具有属性覆盖的自定义<em>复合注解</em>.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Dave Syer
 * @author Chris Beams
 * @see EnableScheduling
 * @see ScheduledAnnotationBeanPostProcessor
 * @see Schedules
 * @since 3.0
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Schedules.class)
public @interface Scheduled {

	/**
	 * 特殊的cron表达式值,表示禁用的触发器:{@value}.
	 * <p>这主要是为了与 ${...} 占位符一起使用,从而允许在外部禁用相应的调度方法.
	 *
	 * @since 5.1
	 */
	String CRON_DISABLED = "-";


	/**
	 * 一种类似于cron的表达式,扩展了常用的的UN*X定义,使其包括秒,分,时,月,日和星期几的触发器.
	 * <p>E.g. {@code "0 * * * * MON-FRI"}是指星期一至星期五每分钟一次(在每分钟的最顶端-第0秒).
	 * <p>特殊值{@link #CRON_DISABLED "-"} 表示禁用cron触发器,
	 * 主要用于由${...}占位符解析的外部指定值.
	 *
	 * @return an expression that can be parsed to a cron schedule
	 * @see org.springframework.scheduling.support.CronSequenceGenerator
	 */
	String cron() default "";

	/**
	 * 将解析cron表达式的时区.默认情况下,此属性为空字符串(即使用服务器的本地时区).
	 *
	 * @return a zone id accepted by {@link java.util.TimeZone#getTimeZone(String)},
	 * or an empty String to indicate the server's default time zone
	 * @see org.springframework.scheduling.support.CronTrigger#CronTrigger(String, java.util.TimeZone)
	 * @see java.util.TimeZone
	 * @since 4.0
	 */
	String zone() default "";

	/**
	 * 在最后一次调用结束和下一次调用开始之间以毫秒为单位执行带注解的方法.
	 * 设置两次调用间隔,以毫秒为单位.
	 *
	 * @return the delay in milliseconds
	 */
	long fixedDelay() default -1;

	/**
	 * Execute the annotated method with a fixed period in milliseconds between the
	 * end of the last invocation and the start of the next.
	 * 在最后一次调用结束和下一次调用开始之间以毫秒为单位执行带注解的方法.
	 *
	 * @return the delay in milliseconds as a String value, e.g. a placeholder
	 * or a {@link java.time.Duration#parse java.time.Duration} compliant value
	 * @since 3.2.2
	 */
	String fixedDelayString() default "";

	/**
	 * invocations.
	 * 在调用之间以毫秒为单位执行带注解的方法.
	 *
	 * @return the period in milliseconds
	 */
	long fixedRate() default -1;

	/**
	 * Execute the annotated method with a fixed period in milliseconds between
	 * invocations.
	 *
	 * @return the period in milliseconds as a String value, e.g. a placeholder
	 * or a {@link java.time.Duration#parse java.time.Duration} compliant value
	 * @since 3.2.2
	 */
	String fixedRateString() default "";

	/**
	 * 在首次执行{@link #fixedRate()}或{@link #fixedDelay()}任务之前要延迟的毫秒数.
	 *
	 * @return the initial delay in milliseconds
	 * @since 3.2
	 */
	long initialDelay() default -1;

	/**
	 * Number of milliseconds to delay before the first execution of a
	 * {@link #fixedRate()} or {@link #fixedDelay()} task.
	 *
	 * @return the initial delay in milliseconds as a String value, e.g. a placeholder
	 * or a {@link java.time.Duration#parse java.time.Duration} compliant value
	 * @since 3.2.2
	 */
	String initialDelayString() default "";

}
