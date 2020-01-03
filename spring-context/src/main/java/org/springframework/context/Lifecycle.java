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
 * 定义start/stop生命周期控制方法的通用接口.典型的用例是控制异步处理.
 * <b>注意:这个接口并不意味着特定的自动启动语义.</b>
 *
 *
 *
 * A common interface defining methods for start/stop lifecycle control.
 * The typical use case for this is to control asynchronous processing.
 * <b>NOTE: This interface does not imply specific auto-startup semantics.
 * Consider implementing {@link SmartLifecycle} for that purpose.</b>
 *
 * <p>可以通过组件(通常是在Spring上下文中定义的Spring bean)和容器(通常是Spring {@link ApplicationContext}本身)来实现.
 * 容器会将启动/停止信号传送到每个容器中应用的所有组件,例如在运行时的停止/重启场景.
 *
 * <p>可以用于直接调用或通过JMX进行管理操作.在通过JMX进行管理操作时,
 * {@link org.springframework.jmx.export.MBeanExporter}
 * 通常使用{@link org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler}
 * 来定义,从而限制活动控制组件对生命周期接口的可见性.
 *
 * <p>注意，当前的{@code Lifecycle}接口只支持<b>顶级的单例bean</b>.
 * 在任何其他组件上,{@code Lifecycle}接口将不会被检测到并因此被忽略.
 * 另外,请注意,这个接口的子接口{@link SmartLifecycle}提供了与应用程序上下文的启动和关闭阶段的复杂集成.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see SmartLifecycle
 * @see ConfigurableApplicationContext
 * @see org.springframework.jms.listener.AbstractMessageListenerContainer
 * @see org.springframework.scheduling.quartz.SchedulerFactoryBean
 */
public interface Lifecycle {

	/**
	 * 启动这个组件
	 * <p>如果组件已经在运行,则不应抛出异常.
	 * <p>对于容器而言,调用此方法,则应该把开始信号传播到应用的所有组件.
	 * @see SmartLifecycle#isAutoStartup()
	 */
	void start();

	/**
	 * Stop this component, typically in a synchronous fashion, such that the component is
	 * fully stopped upon return of this method. Consider implementing {@link SmartLifecycle}
	 * and its {@code stop(Runnable)} variant when asynchronous stop behavior is necessary.
	 * <p>Note that this stop notification is not guaranteed to come before destruction:
	 * On regular shutdown, {@code Lifecycle} beans will first receive a stop notification
	 * before the general destruction callbacks are being propagated; however, on hot
	 * refresh during a context's lifetime or on aborted refresh attempts, a given bean's
	 * destroy method will be called without any consideration of stop signals upfront.
	 * <p>Should not throw an exception if the component is not running (not started yet).
	 * <p>In the case of a container, this will propagate the stop signal to all components
	 * that apply.
	 * @see SmartLifecycle#stop(Runnable)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	void stop();

	/**
	 * Check whether this component is currently running.
	 * <p>In the case of a container, this will return {@code true} only if <i>all</i>
	 * components that apply are currently running.
	 * @return whether the component is currently running
	 */
	boolean isRunning();

}
