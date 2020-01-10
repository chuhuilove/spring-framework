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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指示仅当所有{@linkplain #value 指定条件}匹配时,组件才有资格注册.
 *
 * <p><em>条件</em>是可以在要注册Bean定义之前以编程方式确定的任何状态(有关详细信息,请参见{@link Condition}).
 * <p>{@code @Conditional}注解可以通过以下任何一种方式使用:
 * <ul>
 * <li>作为直接或间接使用{@code @Component}(包括{@link Configuration @Configuration}类)注解的任何类的类型级别注解.</li>
 * <li>作为元注解,用于组合自定义的模板注解,例如在spring boot {@code org.springframework.boot.autoconfigure.condition}包中,
 * 存在大量使用本注解的注解</li>
 * <li>作为任何@Bean方法上的方法级注解,如在{@link Bean @Bean}方法上</li>
 * </ul>
 *
 * <p>如果{@code @Configuration}类上添加了{@code @Conditional}注解,
 * 那么与该类关联的所有{@code @Bean}方法,
 * {@link Import @Import}注解和{@link ComponentScan @ComponentScan}注解都将受到这些条件的约束.
 *
 * <p><strong>注意</strong>: {@code @Conditional}注解不支持继承;
 * 不会考虑超类或重写方法中的任何条件.
 * 为了强制执行这些语义,{@code @Conditional}本身未声明为{@link java.lang.annotation.Inherited @Inherited};
 * 此外,任何使用{@code @Conditional}作为元注解的自定义<em>复合注解</em>都不能声明{@code @Inherited}.
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @see Condition
 * @since 4.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conditional {

	/**
	 * 为了注册组件,必须{@linkplain Condition#matches 匹配}的所有{@link Condition 条件}.
	 */
	Class<? extends Condition>[] value();

}
