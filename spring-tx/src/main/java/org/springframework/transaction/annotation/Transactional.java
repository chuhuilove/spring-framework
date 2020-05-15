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

package org.springframework.transaction.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.TransactionDefinition;

/**
 * 描述单个方法或类上的事务属性.
 *
 * <p>在类级别,此注解默认情况下适用于声明类及其子类的所有方法.
 * 注意,它并不适用于类层次结构上的祖先类;方法需要在本地重新声明,以便参与子类级别的注解.
 *
 *
 * <p>这种注解类型通常可以直接与Spring的
 * {@link org.springframework.transaction.interceptor.RuleBasedTransactionAttribute}类进行比较,
 * 实际上,{@link AnnotationTransactionAttributeSource} 会将数据直接转换为后者,
 * 因此Spring的事务支持代码不必知道注解.
 * 如果没有与异常相关的规则，则将其视为
 * {@link org.springframework.transaction.interceptor.DefaultTransactionAttribute}
 * (在{@link RuntimeException}和{@link Error}上回滚,但在检查的异常上不回滚).
 *
 * <p>有关此批注属性的语义的特定信息,请查阅
 * {@link org.springframework.transaction.TransactionDefinition}
 * 和{@link org.springframework.transaction.interceptor.TransactionAttribute} javadocs.
 *
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 1.2
 * @see org.springframework.transaction.interceptor.TransactionAttribute
 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute
 * @see org.springframework.transaction.interceptor.RuleBasedTransactionAttribute
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

	/**
	 * Alias for {@link #transactionManager}.
	 * @see #transactionManager
	 */
	@AliasFor("transactionManager")
	String value() default "";

	/**
	 * A <em>qualifier</em> value for the specified transaction.
	 * <p>May be used to determine the target transaction manager,
	 * matching the qualifier value (or the bean name) of a specific
	 * {@link org.springframework.transaction.PlatformTransactionManager}
	 * bean definition.
	 * @since 4.2
	 * @see #value
	 */
	@AliasFor("value")
	String transactionManager() default "";

	/**
	 * 事务传播类型
	 * <p>默认是{@link Propagation#REQUIRED}.
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#getPropagationBehavior()
	 */
	Propagation propagation() default Propagation.REQUIRED;

	/**
	 * 事务隔离级别
	 * <p>默认是{@link Isolation#DEFAULT}.
	 * <p>专门设计用于{@link Propagation#REQUIRED}或{@link Propagation#REQUIRES_NEW},因为它只适用于新启动的事务.
	 * 如果你希望在参与具有不同隔离级别的现有事务时拒绝隔离级别声明,
	 * 请考虑将事务管理器上的"validateExistingTransactions"标志切换为"true".
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#getIsolationLevel()
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
	 */
	Isolation isolation() default Isolation.DEFAULT;

	/**
	 * The timeout for this transaction (in seconds).
	 * <p>Defaults to the default timeout of the underlying transaction system.
	 * <p>Exclusively designed for use with {@link Propagation#REQUIRED} or
	 * {@link Propagation#REQUIRES_NEW} since it only applies to newly started
	 * transactions.
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#getTimeout()
	 */
	int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;

	/**
	 * 如果事务实际上是只读的,
	 * 则可以将其设置为{@code true}的布尔标志,从而允许在运行时进行相应的优化.
	 * <p>默认为{@code false}.
	 * <p>这只是作为实际事务子系统的提示;它<i>不一定</i>导致写访问尝试失败.
	 *
	 * A transaction manager which cannot interpret the read-only hint will
	 * <i>not</i> throw an exception when asked for a read-only transaction
	 * but rather silently ignore the hint.
	 * @see org.springframework.transaction.interceptor.TransactionAttribute#isReadOnly()
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
	 */
	boolean readOnly() default false;

	/**
	 * 定义零(0)个或多个异常{@link Class classes},这些异常必须是{@link Throwable}的子类,指示哪些异常类型必须导致事务回滚.
	 * <p>默认情况下,事务将在{@link RuntimeException}和{@link Error}上回滚,但在受控异常(业务异常)上不回滚.
	 * 参见{@link org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)}获得详细解释.
	 * <p>这是构造回滚规则(与{@link #rollbackForClassName}相比)的首选方法,该规则匹配异常类及其子类.
	 * <p>类似于{@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(Class clazz)}.
	 * @see #rollbackForClassName
	 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
	 */
	Class<? extends Throwable>[] rollbackFor() default {};

	/**
	 * 定义零(0)个或多个异常名称(对于必须是{@link Throwable}的子类的异常),指示必须导致事务回滚的异常类型.
	 * 一半情况下,还是使用{@linkplain #rollbackFor()}.
	 * <p>This can be a substring of a fully qualified class name, with no wildcard
	 * support at present. For example, a value of {@code "ServletException"} would
	 * match {@code javax.servlet.ServletException} and its subclasses.
	 * <p><b>NB:</b> Consider carefully how specific the pattern is and whether
	 * to include package information (which isn't mandatory). For example,
	 * {@code "Exception"} will match nearly anything and will probably hide other
	 * rules. {@code "java.lang.Exception"} would be correct if {@code "Exception"}
	 * were meant to define a rule for all checked exceptions. With more unusual
	 * {@link Exception} names such as {@code "BaseBusinessException"} there is no
	 * need to use a FQN.
	 * <p>Similar to {@link org.springframework.transaction.interceptor.RollbackRuleAttribute#RollbackRuleAttribute(String exceptionName)}.
	 * @see #rollbackFor
	 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
	 */
	String[] rollbackForClassName() default {};

	/**
	 *
	 * 定义零(0)个或更多的异常{@link Class Classs},
	 * 它们必须是{@link Throwable}的子类,指示哪些异常类型必须<b>不</ b>引起事务回滚.
	 * <p>这是构造回滚规则(与{@link #noRollbackForClassName}相比)的首选方法,该规则与异常类及其子类匹配.
	 * <p>类似于{@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(Class clazz)}.
	 * @see #noRollbackForClassName
	 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
	 */
	Class<? extends Throwable>[] noRollbackFor() default {};

	/**
	 * Defines zero (0) or more exception names (for exceptions which must be a
	 * subclass of {@link Throwable}) indicating which exception types must <b>not</b>
	 * cause a transaction rollback.
	 * <p>See the description of {@link #rollbackForClassName} for further
	 * information on how the specified names are treated.
	 * <p>Similar to {@link org.springframework.transaction.interceptor.NoRollbackRuleAttribute#NoRollbackRuleAttribute(String exceptionName)}.
	 * @see #noRollbackFor
	 * @see org.springframework.transaction.interceptor.DefaultTransactionAttribute#rollbackOn(Throwable)
	 */
	String[] noRollbackForClassName() default {};

}
