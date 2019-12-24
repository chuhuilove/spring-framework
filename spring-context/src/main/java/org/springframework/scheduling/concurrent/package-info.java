/**
 * 为{@code java.util.concurrent}和{@code javax.enterprise.concurrent}两种包使用的便捷类,
 * 允许在Spring上下文中设置ThreadPoolExecutor或ScheduledThreadPoolExecutor作为bean.
 * 提供对本地{@code java.util.concurrent}接口以及Spring {@code TaskExecutor}机制的支持.
 */
@NonNullApi
@NonNullFields
package org.springframework.scheduling.concurrent;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
