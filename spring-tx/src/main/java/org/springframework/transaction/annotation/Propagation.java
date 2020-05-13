/*
 * Copyright 2002-2019 the original author or authors.
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

import org.springframework.transaction.TransactionDefinition;

/**
 * 枚举类,表示与{@link TransactionDefinition}接口对应的{@link Transactional}注解一起使用的事务传播行为.
 *
 *
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @since 1.2
 */
public enum Propagation {

	/**
	 * 支持当前事务,如果不存在则创建一个新事务.
	 * 类似于同名的EJB事务属性.
	 * <p>这是Transactional注解的默认设置.
	 */
	REQUIRED(TransactionDefinition.PROPAGATION_REQUIRED),

	/**
	 * 支持当前事务,如果不存在则非事务执行.
	 * 类似于同名的EJB事务属性.
	 * <p>注意: 对于具有事务同步的事务管理器,{@code SUPPORTS}与根本没有事务略有不同,
	 * 因为它定义了将应用同步的事务范围.
	 * 因此,相同的资源(JDBC连接、Hibernate会话等)将在整个指定的范围内共享.
	 * 注意,这取决于事务管理器的实际同步配置.
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization
	 */
	SUPPORTS(TransactionDefinition.PROPAGATION_SUPPORTS),

	/**
	 * 支持当前事务,如果不存在则抛出异常.
	 * 类似于同名的EJB事务属性.
	 */
	MANDATORY(TransactionDefinition.PROPAGATION_MANDATORY),

	/**
	 * 创建一个新事务,并暂停当前事务(如果存在).
	 * 类似于同名的EJB事务属性.
	 * <p><b>注意:</b> Actual transaction suspension will not work out-of-the-box
	 * on all transaction managers. This in particular applies to
	 * {@link org.springframework.transaction.jta.JtaTransactionManager},
	 * which requires the {@code javax.transaction.TransactionManager} to be
	 * made available to it (which is server-specific in standard Java EE).
	 * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
	 */
	REQUIRES_NEW(TransactionDefinition.PROPAGATION_REQUIRES_NEW),

	/**
	 * 以非事务方式执行,如果当前事务存在,则挂起当前事务.
	 * 类似于同名的EJB事务属性.
	 * <p><b>注意:</b> 实际的事务挂起不会在所有事务管理器上开箱即用.
	 * 这尤其适用于{@link org.springframework.transaction.jta.JtaTransactionManager},
	 * 它要求{{@code javax.transaction.TransactionManager}对其可用(在标准Java EE中特定于服务器).
	 *
	 * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
	 */
	NOT_SUPPORTED(TransactionDefinition.PROPAGATION_NOT_SUPPORTED),

	/**
	 * 非事务执行,如果存在事务,则引发异常.
	 * 类似于同名的EJB事务属性.
	 */
	NEVER(TransactionDefinition.PROPAGATION_NEVER),

	/**
	 * 如果当前事务存在,则在嵌套事务中执行,否则,其行为类似于{@code REQUIRED}.
	 * EJB中没有类似的功能.
	 * <p>注意: 实际创建嵌套事务将仅在特定事务管理器上起作用.开箱即用,
	 * 这仅适用于JDBC DataSourceTransactionManager.
	 * 一些JTA提供程序也可能支持嵌套事务.
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
	 */
	NESTED(TransactionDefinition.PROPAGATION_NESTED);


	private final int value;


	Propagation(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

}
