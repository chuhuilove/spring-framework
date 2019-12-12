/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.core;

/**
 * {@code Ordered}是一个接口,它可以由应该<em>orderable</em>的对象(例如集合中的对象)来实现.
 *
 * <p>
 * 实际的{@link #getOrder() order}可以被解释为优先级,第一个对象(具有最低的order值)具有最高的优先级.
 *
 * <p>注意,此接口还有一个<em>优先级</em>标记:{@link PriorityOrdered}
 *
 * 由{@code PriorityOrdered}对象表示的Order值始终在由<em>普通</em>{@link Ordered}对象表示的相同Order值之前应用.
 *
 * <p>Consult the Javadoc for {@link OrderComparator} for details on the
 * sort semantics for non-ordered objects.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 07.04.2003
 * @see PriorityOrdered
 * @see OrderComparator
 * @see org.springframework.core.annotation.Order
 * @see org.springframework.core.annotation.AnnotationAwareOrderComparator
 */
public interface Ordered {

	/**
	 * Useful constant for the highest precedence value.
	 * @see java.lang.Integer#MIN_VALUE
	 */
	int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

	/**
	 * Useful constant for the lowest precedence value.
	 * @see java.lang.Integer#MAX_VALUE
	 */
	int LOWEST_PRECEDENCE = Integer.MAX_VALUE;


	/**
	 * 获得这个对象的order值.
	 * <p>值越大,表示优先级越低.因此,最小的值将拥有最高的优先级.(有点类似Servlet {@code load-on-startup}的值)
	 * <p>
	 * 相同的order值,将导致受影响的对象任意排序.
	 * @return the order value
	 * @see #HIGHEST_PRECEDENCE
	 * @see #LOWEST_PRECEDENCE
	 */
	int getOrder();

}
