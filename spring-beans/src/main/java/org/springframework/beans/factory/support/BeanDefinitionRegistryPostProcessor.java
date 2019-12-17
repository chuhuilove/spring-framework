/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;

import java.util.List;

/**
 * 对标准{@link BeanFactoryPostProcessor} SPI的扩展,
 * 允许在常规BeanFactoryPostProcessor检测开始<i>之前</i>注册更多的bean定义(BeanDefinition).
 * 在{@link org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory, List)}
 * 中有体现.特别是,BeanDefinitionRegistryPostProcessor可以注册其他bean定义(BeanDefinition),这些定义又定义了BeanFactoryPostProcessor实例.
 *
 * Extension to the standard {@link BeanFactoryPostProcessor} SPI, allowing for
 * the registration of further bean definitions <i>before</i> regular
 * BeanFactoryPostProcessor detection kicks in. In particular,
 * BeanDefinitionRegistryPostProcessor may register further bean definitions
 * which in turn define BeanFactoryPostProcessor instances.
 *
 * <p>
 * 解析这个接口的实现,是很早的,甚至比解析其父接口{@link BeanFactoryPostProcessor}还早
 *
 * <p>
 * 1. 执行实现了{@link BeanDefinitionRegistryPostProcessor}的类,最早在{@link org.springframework.context.support.AbstractApplicationContext#refresh()}
 * 函数中,调用{@link org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory)}中.
 * 先执行了已经加入到{@link org.springframework.context.support.AbstractApplicationContext#beanFactoryPostProcessors}中的类,以添加更多的定义.
 *
 * <p>
 * 2. 从bean工厂中获取实现了{@code BeanDefinitionRegistryPostProcessor}又实现了{@code PriorityOrdered}接口的类.
 * 根据{@link PriorityOrdered#getOrder()}的值进行排序.注意,默认注册进去的一个类{@link org.springframework.context.annotation.ConfigurationClassPostProcessor}的优先级最低.
 * 自定义的实现,若{@link PriorityOrdered#getOrder()}的值和{@link org.springframework.context.annotation.ConfigurationClassPostProcessor}的值相等,则自定义的实现优先级最低.
 *
 * <p>
 * 3. 从bean工厂中获取实现了{@code BeanDefinitionRegistryPostProcessor}又实现了{@code Ordered}接口的类
 *
 *
 * @author Juergen Hoeller
 * @since 3.0.1
 * @see org.springframework.context.annotation.ConfigurationClassPostProcessor
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

	/**
	 * 在标准初始化之后,修改应用程序上下文的内部bean定义注册表.
	 * 所有常规bean定义都将被加载,但尚未实例化任何bean.
	 * 这允许在下一个post-processing开始之前添加更多的bean定义.
	 * Modify the application context's internal bean definition registry after its
	 * standard initialization. All regular bean definitions will have been loaded,
	 * but no beans will have been instantiated yet. This allows for adding further
	 * bean definitions before the next post-processing phase kicks in.
	 * @param registry the bean definition registry used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
