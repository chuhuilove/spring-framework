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

package org.springframework.context.annotation;

/**
 * 与{@link Condition}一起使用时,可以提供更细粒度控制的条件.允许某些{@link Condition Conditions}在匹配时根据配置进行调整.
 * 允许根据配置阶段匹配某些条件.
 * 例如,检查bean是否已经注册的条件可能选择仅在{@link ConfigurationPhase#REGISTER_BEAN REGISTER_BEAN} {@link ConfigurationPhase}期间进行计算.
 *
 * @author Phillip Webb
 * @since 4.0
 * @see Configuration
 */
public interface ConfigurationCondition extends Condition {

	/**
	 * Return the {@link ConfigurationPhase} in which the condition should be evaluated.
	 */
	ConfigurationPhase getConfigurationPhase();


	/**
	 * The various configuration phases where the condition could be evaluated.
	 */
	enum ConfigurationPhase {

		/**
		 * 应该在解析{@code @Configuration}类时对{@link Condition}进行评估.
		 * The {@link Condition} should be evaluated as a {@code @Configuration}
		 * class is being parsed.
		 * <p>如果此时条件不匹配，则不会添加{@code @Configuration}类.
		 * If the condition does not match at this point, the {@code @Configuration}
		 * class will not be added.
		 */
		PARSE_CONFIGURATION,

		/**
		 * 在添加常规(非{@code @Configuration})bean时,应该对{@link Condition}进行评估.
		 * 该条件不会阻止{@code @Configuration}类被添加.
		 * The {@link Condition} should be evaluated when adding a regular
		 * (non {@code @Configuration}) bean. The condition will not prevent
		 * {@code @Configuration} classes from being added.
		 * <p>在评估条件时,将解析所有{@code @Configuration}.At the time that the condition is evaluated, all {@code @Configuration}s
		 * will have been parsed.
		 */
		REGISTER_BEAN
	}

}
