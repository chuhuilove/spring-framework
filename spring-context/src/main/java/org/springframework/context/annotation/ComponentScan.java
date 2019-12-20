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

package org.springframework.context.annotation;

import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.filter.TypeFilter;

import java.lang.annotation.*;

/**
 * 配置组件扫描指令以与@{@link Configuration}类一起使用.
 * 提供与Spring XML的{@code <context:component-scan>}元素并行的支持.
 *
 * <p>可以指定{@link #basePackageClasses}或{@link #basePackages}(或其别名{@link #value})来定义要扫描的特定程序包.
 * 如果未定义特定的程序包,则将从声明此注解的类的程序包中进行扫描.
 *
 * <p>注意,{@code <context:component-scan>}元素具有注解配置属性;然而,@{@link ComponentScan}这个注解没有.
 * 这是因为在几乎所有情况下,使用{@code @ComponentScan}时,
 * 都假定使用默认的注解配置处理(例如,处理{@code @Autowired}和其他诸如{@code @Resource}).
 * 此外,在使用{@link AnnotationConfigApplicationContext}时,注解配置处理器始终会被注册,
 * 这意味着在{@code @ComponentScan}级别禁用它们的任何尝试都将被忽略.
 * <p>
 * 在{@link ConfigurationClassParser#doProcessConfigurationClass(ConfigurationClass, ConfigurationClassParser.SourceClass)}调用解析.
 * 具体解析位置在{@link ComponentScanAnnotationParser#parse(AnnotationAttributes, String)}
 *
 * <p>See {@link Configuration @Configuration}'s Javadoc for usage examples.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @see Configuration
 * @since 3.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(ComponentScans.class)
public @interface ComponentScan {

	/**
	 * Alias for {@link #basePackages}.
	 * <p>Allows for more concise annotation declarations if no other attributes
	 * are needed &mdash; for example, {@code @ComponentScan("org.my.pkg")}
	 * instead of {@code @ComponentScan(basePackages = "org.my.pkg")}.
	 */
	@AliasFor("basePackages")
	String[] value() default {};

	/**
	 * Base packages to scan for annotated components.
	 * <p>{@link #value} is an alias for (and mutually exclusive with) this
	 * attribute.
	 * <p>Use {@link #basePackageClasses} for a type-safe alternative to
	 * String-based package names.
	 */
	@AliasFor("value")
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #basePackages} for specifying the packages
	 * to scan for annotated components. The package of each class specified will be scanned.
	 * <p>Consider creating a special no-op marker class or interface in each package
	 * that serves no purpose other than being referenced by this attribute.
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * The {@link BeanNameGenerator} class to be used for naming detected components
	 * within the Spring container.
	 * 设置命名生产器.这个命名生产器的作用范围只是定义的扫描包里的内容.
	 * <p>The default value of the {@link BeanNameGenerator} interface itself indicates
	 * that the scanner used to process this {@code @ComponentScan} annotation should
	 * use its inherited bean name generator, e.g. the default
	 * {@link AnnotationBeanNameGenerator} or any custom instance supplied to the
	 * application context at bootstrap time.
	 *
	 * @see AnnotationConfigApplicationContext#setBeanNameGenerator(BeanNameGenerator)
	 */
	Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

	/**
	 * {@link ScopeMetadataResolver}用于解析检测到的组件的范围.
	 */
	Class<? extends ScopeMetadataResolver> scopeResolver() default AnnotationScopeMetadataResolver.class;

	/**
	 * Indicates whether proxies should be generated for detected components, which may be
	 * necessary when using scopes in a proxy-style fashion.
	 * <p>The default is defer to the default behavior of the component scanner used to
	 * execute the actual scan.
	 * <p>Note that setting this attribute overrides any value set for {@link #scopeResolver}.
	 *
	 * @see ClassPathBeanDefinitionScanner#setScopedProxyMode(ScopedProxyMode)
	 */
	ScopedProxyMode scopedProxy() default ScopedProxyMode.DEFAULT;

	/**
	 * 控制有资格进行组件检测的class文件.
	 * <p>考虑使用{@link #includeFilters}和{@link #excludeFilters}以获得更灵活的方法
	 */
	String resourcePattern() default ClassPathScanningCandidateComponentProvider.DEFAULT_RESOURCE_PATTERN;

	/**
	 * Indicates whether automatic detection of classes annotated with {@code @Component}
	 * {@code @Repository}, {@code @Service}, or {@code @Controller} should be enabled.
	 */
	boolean useDefaultFilters() default true;

	/**
	 * Specifies which types are eligible for component scanning.
	 * <p>Further narrows the set of candidate components from everything in {@link #basePackages}
	 * to everything in the base packages that matches the given filter or filters.
	 * <p>Note that these filters will be applied in addition to the default filters, if specified.
	 * Any type under the specified base packages which matches a given filter will be included,
	 * even if it does not match the default filters (i.e. is not annotated with {@code @Component}).
	 *
	 * @see #resourcePattern()
	 * @see #useDefaultFilters()
	 */
	Filter[] includeFilters() default {};

	/**
	 * 指定哪些类型不适合组件扫描.
	 * <p>
	 * 如果过滤掉指定的类,则可以写成:excludeFilters ={ @Filter(type=FilterType.ASSIGNABLE_TYPE,classes = BootstrapApplication.class) }
	 * 注意,过滤类,则type设置为{@link FilterType.ASSIGNABLE_TYPE};
	 * 如果之前BootstrapApplication已经添加到beanFactory中了,则再次扫描,过滤,则不会移除已经存在beanFactory中的类的.
	 * <p>
	 * 若之前已经将某个类A过滤掉了,但是在第二次扫描的时候,没有将类A过滤掉,则最终beanFactory中还是会出现类A.
	 * 最典型的例子就是:
	 * <p>
	 * &#064;Configuration
	 * &#064;Order(Ordered.LOWEST_PRECEDENCE-1)
	 * &#064;ComponentScan("com.chuhui.springbootdebug")
	 * public class AppConfigSecond {
	 * <p>
	 * }
	 * 和
	 * &#064;Configuration
	 * &#064;Order(Ordered.LOWEST_PRECEDENCE-2)
	 * &#064;ComponentScan(value = "com.chuhui.springbootdebug",excludeFilters ={&#064;Filter(type=FilterType.ASSIGNABLE_TYPE,classes = BootstrapApplication.class) })
	 * public class AppConfigMain {
	 * }
	 * <p>
	 * {@code AppConfigMain}的优先级比较高,先执行,在扫描的过程中,会过滤掉BootstrapApplication类,
	 * 但是在执行{@code AppConfigSecond}的时候,会将BootstrapApplication再重新添加进来.
	 *
	 * @see #resourcePattern
	 */
	Filter[] excludeFilters() default {};

	/**
	 * Specify whether scanned beans should be registered for lazy initialization.
	 * 指定被扫描的bean是否应该延迟初始化.
	 * <p>Default is {@code false}; switch this to {@code true} when desired.
	 *
	 * @since 4.1
	 */
	boolean lazyInit() default false;


	/**
	 * Declares the type filter to be used as an {@linkplain ComponentScan#includeFilters
	 * include filter} or {@linkplain ComponentScan#excludeFilters exclude filter}.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({})
	@interface Filter {

		/**
		 * The type of filter to use.
		 * <p>Default is {@link FilterType#ANNOTATION}.
		 *
		 * @see #classes
		 * @see #pattern
		 */
		FilterType type() default FilterType.ANNOTATION;

		/**
		 * Alias for {@link #classes}.
		 *
		 * @see #classes
		 */
		@AliasFor("classes")
		Class<?>[] value() default {};

		/**
		 * The class or classes to use as the filter.
		 * <p>The following table explains how the classes will be interpreted
		 * based on the configured value of the {@link #type} attribute.
		 * <table border="1">
		 * <tr><th>{@code FilterType}</th><th>Class Interpreted As</th></tr>
		 * <tr><td>{@link FilterType#ANNOTATION ANNOTATION}</td>
		 * <td>the annotation itself</td></tr>
		 * <tr><td>{@link FilterType#ASSIGNABLE_TYPE ASSIGNABLE_TYPE}</td>
		 * <td>the type that detected components should be assignable to</td></tr>
		 * <tr><td>{@link FilterType#CUSTOM CUSTOM}</td>
		 * <td>an implementation of {@link TypeFilter}</td></tr>
		 * </table>
		 * <p>When multiple classes are specified, <em>OR</em> logic is applied
		 * &mdash; for example, "include types annotated with {@code @Foo} OR {@code @Bar}".
		 * <p>Custom {@link TypeFilter TypeFilters} may optionally implement any of the
		 * following {@link org.springframework.beans.factory.Aware Aware} interfaces, and
		 * their respective methods will be called prior to {@link TypeFilter#match match}:
		 * <ul>
		 * <li>{@link org.springframework.context.EnvironmentAware EnvironmentAware}</li>
		 * <li>{@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware}
		 * <li>{@link org.springframework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}
		 * <li>{@link org.springframework.context.ResourceLoaderAware ResourceLoaderAware}
		 * </ul>
		 * <p>Specifying zero classes is permitted but will have no effect on component
		 * scanning.
		 *
		 * @see #value
		 * @see #type
		 * @since 4.2
		 */
		@AliasFor("value")
		Class<?>[] classes() default {};

		/**
		 * The pattern (or patterns) to use for the filter, as an alternative
		 * to specifying a Class {@link #value}.
		 * <p>If {@link #type} is set to {@link FilterType#ASPECTJ ASPECTJ},
		 * this is an AspectJ type pattern expression. If {@link #type} is
		 * set to {@link FilterType#REGEX REGEX}, this is a regex pattern
		 * for the fully-qualified class names to match.
		 *
		 * @see #type
		 * @see #classes
		 */
		String[] pattern() default {};

	}

}
