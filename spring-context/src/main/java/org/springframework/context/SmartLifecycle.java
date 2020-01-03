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
 *
 * {@link Lifecycle}接口的扩展,用于那些需要在ApplicationContext刷新和/或按特定顺序关闭时启动的对象.
 * {@link #isAutoStartup()}返回值指示是否应在刷新上下文时启动此对象.
 * 接受回调的{@link #stop(Runnable)}方法对于具有异步关闭过程的对象很有用.
 * 此接口的任何实现都必须在关闭完成时调用回调的{@code run()}方法,以避免在整个ApplicationContext关闭中不必要的延迟。
 *
 *
 * <p>该接口扩展了{@link Phased},{@link #getPhase()}方法的返回值指示了启动和停止该生命周期组件的阶段.
 * 启动过程以<i>最低</i>的phase值开始,以<i>最高</i>的phase值结束({@code Integer.MIN_VALUE}是最低的,{@code Integer.MAX_VALUE}是最高的).
 * 关闭过程将应用相反的顺序.具有相同值的任何组件将在同一phase中任意排序.
 *
 * <p>示例:如果组件B依赖于已经启动的组件A,那么组件A的phase值应该低于组件B.
 * 在关闭过程中,组件B将在组件A之前停止.
 *
 * <p>任何显式的"depends-on"关系都优先于phase顺序,这样依赖项bean总是在依赖项之后开始,总是在依赖项之前停止.
 *
 * <p>上下文中的任何没有实现{@code SmartLifecycle}的{@code Lifecycle}组件都将被视为阶段值为0.
 * 这样,如果{@code SmartLifecycle}的phase值为负,那么它可以在这些{@code Lifecycle}组件之前开始,
 * 如果阶段值为正,则可以在这些组件之后开始.
 *
 * <p>注意,由于{@code SmartLifecycle}支持自动启动,所以在任何情况下,{@code SmartLifecycle} bean实例通常在应用程序上下文启动时初始化.
 * 因此,bean定义的lazy-init标志对{@code SmartLifecycle} bean的实际影响非常有限.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0
 * @see LifecycleProcessor
 * @see ConfigurableApplicationContext
 */
public interface SmartLifecycle extends Lifecycle, Phased {

	/**
	 * The default phase for {@code SmartLifecycle}: {@code Integer.MAX_VALUE}.
	 * <p>This is different from the common phase 0 associated with regular
	 * {@link Lifecycle} implementations, putting the typically auto-started
	 * {@code SmartLifecycle} beans into a separate later shutdown phase.
	 * @since 5.1
	 * @see #getPhase()
	 * @see org.springframework.context.support.DefaultLifecycleProcessor#getPhase(Lifecycle)
	 */
	int DEFAULT_PHASE = Integer.MAX_VALUE;


	/**
	 * Returns {@code true} if this {@code Lifecycle} component should get
	 * started automatically by the container at the time that the containing
	 * {@link ApplicationContext} gets refreshed.
	 * <p>A value of {@code false} indicates that the component is intended to
	 * be started through an explicit {@link #start()} call instead, analogous
	 * to a plain {@link Lifecycle} implementation.
	 * <p>The default implementation returns {@code true}.
	 * @see #start()
	 * @see #getPhase()
	 * @see LifecycleProcessor#onRefresh()
	 * @see ConfigurableApplicationContext#refresh()
	 */
	default boolean isAutoStartup() {
		return true;
	}

	/**
	 * Indicates that a Lifecycle component must stop if it is currently running.
	 * <p>The provided callback is used by the {@link LifecycleProcessor} to support
	 * an ordered, and potentially concurrent, shutdown of all components having a
	 * common shutdown order value. The callback <b>must</b> be executed after
	 * the {@code SmartLifecycle} component does indeed stop.
	 * <p>The {@link LifecycleProcessor} will call <i>only</i> this variant of the
	 * {@code stop} method; i.e. {@link Lifecycle#stop()} will not be called for
	 * {@code SmartLifecycle} implementations unless explicitly delegated to within
	 * the implementation of this method.
	 * <p>The default implementation delegates to {@link #stop()} and immediately
	 * triggers the given callback in the calling thread. Note that there is no
	 * synchronization between the two, so custom implementations may at least
	 * want to put the same steps within their common lifecycle monitor (if any).
	 * @see #stop()
	 * @see #getPhase()
	 */
	default void stop(Runnable callback) {
		stop();
		callback.run();
	}

	/**
	 * Return the phase that this lifecycle object is supposed to run in.
	 * <p>The default implementation returns {@link #DEFAULT_PHASE} in order to
	 * let stop callbacks execute after regular {@code Lifecycle} implementations.
	 * @see #isAutoStartup()
	 * @see #start()
	 * @see #stop(Runnable)
	 * @see org.springframework.context.support.DefaultLifecycleProcessor#getPhase(Lifecycle)
	 */
	@Override
	default int getPhase() {
		return DEFAULT_PHASE;
	}

}
