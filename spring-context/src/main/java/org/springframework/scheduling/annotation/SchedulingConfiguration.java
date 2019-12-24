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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.config.TaskManagementConfigUtils;

/**
 * 注册一个{@link ScheduledAnnotationBeanPostProcessor}Bean,
 * 使得该bean能够处理Spring的@{@link Scheduled}注解的{@code @Configuration}类.
 *
 *
 * <p>使用{@link EnableScheduling @EnableScheduling}注解时,将自动导入此配置类.
 *
 * @author Chris Beams
 * @since 3.1
 * @see EnableScheduling
 * @see ScheduledAnnotationBeanPostProcessor
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SchedulingConfiguration {

	/**
	 * 设置beanName为:org.springframework.context.annotation.internalScheduledAnnotationProcessor
	 */
	@Bean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public ScheduledAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
		return new ScheduledAnnotationBeanPostProcessor();
	}

}
