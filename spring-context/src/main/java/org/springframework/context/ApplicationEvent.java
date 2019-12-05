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

import java.util.EventObject;

/**
 * Class to be extended by all application events. Abstract as it
 * doesn't make sense for generic events to be published directly.
 *
 * <p>
 * 从Spring 4.2开始,event基础设施得到了显著的改进,
 * 并提供了基于注解的模型以及发布任意事件的能力(即不需要从{@code ApplicationEvent}扩展的对象).
 * 当这样一个对象被发布时,我们将它包装成一个事件.
 *
 * <p>
 * 1. {@code ContextRefreshedEvent}
 * 	  在初始化或刷新{@code ApplicationContext}时发布(例如,通过使用{@code ConfigurableApplicationContext}接口上的{@code refresh()}方法).
 * 	  在这里,"初始化"意味着加载所有bean,检测并激活后处理器bean,预实例化单例,以及{@code ApplicationContext}对象已准备好使用.
 * 	  只要上下文没有关闭,就可以多次触发刷新,前提是所选的{@code ApplicationContext}实际上支持这样的"热"更新.
 * 	  例如,{@code XmlWebApplicationContext}支持热更新,但是{@code GenericApplicationContext}不支持.
 * 2. {@code ContextStartedEvent}
 * 3. {@code ContextStoppedEvent}
 * 4. {@code ContextClosedEvent}
 * 5. {@code RequestHandledEvent}
 *    一个特定于Web的事件,告诉所有Bean HTTP请求已得到服务.此事件在请求完成后发布.
 *    此事件仅适用于使用Spring的{@code DispatcherServlet}的web应用程序.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class ApplicationEvent extends EventObject {

	/** use serialVersionUID from Spring 1.2 for interoperability. */
	private static final long serialVersionUID = 7099057708183571937L;

	/** System time when the event happened. */
	private final long timestamp;


	/**
	 * Create a new ApplicationEvent.
	 * @param source the object on which the event initially occurred (never {@code null})
	 */
	public ApplicationEvent(Object source) {
		super(source);
		this.timestamp = System.currentTimeMillis();
	}


	/**
	 * Return the system time in milliseconds when the event happened.
	 */
	public final long getTimestamp() {
		return this.timestamp;
	}

}
