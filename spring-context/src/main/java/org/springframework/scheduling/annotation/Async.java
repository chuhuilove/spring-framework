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

package org.springframework.scheduling.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将方法标记为<i>异步</i>执行的候选者的注解.也可以在类型级别使用,当这个注解应用到类型上时,该类型下所有的方法都被视为异步方法.
 *
 * <p>就目标方法签名而言,支持任何参数类型.但是,返回类型被限制为{@code void}或{@link java.util.concurrent.Future}.
 * 在后一种情况下,可以声明更具体的{@link org.springframework.util.concurrent.ListenableFuture}
 * 或{@link java.util.concurrent.CompletableFuture}类型,
 * 这些类型允许与异步任务进行更丰富的交互,并允许立即与进一步的处理步骤进行组合.
 *
 * <p>从代理返回的{@code Future}句柄将是实际的异步{@code Future},可用于跟踪异步方法执行的结果.
 * 但是,由于目标方法需要实现相同的签名,因此它必须返回一个临时的{@code Future}句柄,
 * 该句柄仅将值传递给以下对象:Spring的{@link AsyncResult},EJB 3.1的{@link javax.ejb.AsyncResult}
 * 或{@link java.util.concurrent.CompletableFuture#completedFuture(Object)}.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @see AnnotationAsyncExecutionInterceptor
 * @see AsyncAnnotationAdvisor
 * @since 3.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {

	/**
	 * 指定的异步操作的限定符值.
	 * <p>可用于确定执行此方法时要使用的目标执行程序,以匹配特定{@link java.util.concurrent.Executor Executor}
	 * 或{@link org.springframework.core.task.TaskExecutor TaskExecutor} bean 定义的限定符值(或bean名).
	 * <p>当在类级别{@code @Async}注解上指定时,表示给定的执行器应该用于类中的所有方法.
	 * 方法级别使用{@code Async#value}总是覆盖类级别设置的任何值.
	 *
	 * @since 3.1.2
	 */
	String value() default "";

}
