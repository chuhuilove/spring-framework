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

package org.springframework.http.converter.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/**
 * {@link org.springframework.http.converter.HttpMessageConverter}的实现,使用
 * <a href="https://github.com/FasterXML/jackson">Jackson 2.x's</a> {@link ObjectMapper}来读写JSON.
 *
 * <p>这个converter可以被用于绑定到类型化的bean或未类型化的{@code HashMap}实例
 *
 * <p>默认情况下,这个converter支持{@code UTF-8}字符集的{@code application/json} and {@code application/*+json}.
 * 可以通过重写{@link #setSupportedMediaTypes supportedMediaTypes}方法来设置属性{@code supportedMediaTypes}的值.
 *
 * <p> 默认构造函数使用{@link Jackson2ObjectMapperBuilder}提供的默认配置.
 *
 * <p> 从Spring 5.0开始与Jackson 2.9以及更高版本兼容.
 *
 *
 * Implementation of {@link org.springframework.http.converter.HttpMessageConverter} that can read and
 * write JSON using <a href="https://github.com/FasterXML/jackson">Jackson 2.x's</a> {@link ObjectMapper}.
 *
 *
 *
 * <p>This converter can be used to bind to typed beans, or untyped {@code HashMap} instances.
 *
 * <p>By default, this converter supports {@code application/json} and {@code application/*+json}
 * with {@code UTF-8} character set. This can be overridden by setting the
 * {@link #setSupportedMediaTypes supportedMediaTypes} property.
 *
 * <p>The default constructor uses the default configuration provided by {@link Jackson2ObjectMapperBuilder}.
 *
 * <p>Compatible with Jackson 2.9 and higher, as of Spring 5.0.
 *
 * @author Arjen Poutsma
 * @author Keith Donald
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @since 3.1.2
 */
public class MappingJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {

	@Nullable
	private String jsonPrefix;


	/**
	 * Construct a new {@link MappingJackson2HttpMessageConverter} using default configuration
	 * provided by {@link Jackson2ObjectMapperBuilder}.
	 */
	public MappingJackson2HttpMessageConverter() {
		this(Jackson2ObjectMapperBuilder.json().build());
	}

	/**
	 * Construct a new {@link MappingJackson2HttpMessageConverter} with a custom {@link ObjectMapper}.
	 * You can use {@link Jackson2ObjectMapperBuilder} to build it easily.
	 * @see Jackson2ObjectMapperBuilder#json()
	 */
	public MappingJackson2HttpMessageConverter(ObjectMapper objectMapper) {
		/**
		 * 支持的媒体类型为application/json和application/*+json
		 */
		super(objectMapper, MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
	}


	/**
	 * Specify a custom prefix to use for this view's JSON output.
	 * Default is none.
	 * @see #setPrefixJson
	 */
	public void setJsonPrefix(String jsonPrefix) {
		this.jsonPrefix = jsonPrefix;
	}

	/**
	 * Indicate whether the JSON output by this view should be prefixed with ")]}', ". Default is false.
	 * <p>Prefixing the JSON string in this manner is used to help prevent JSON Hijacking.
	 * The prefix renders the string syntactically invalid as a script so that it cannot be hijacked.
	 * This prefix should be stripped before parsing the string as JSON.
	 * @see #setJsonPrefix
	 */
	public void setPrefixJson(boolean prefixJson) {
		this.jsonPrefix = (prefixJson ? ")]}', " : null);
	}


	@Override
	protected void writePrefix(JsonGenerator generator, Object object) throws IOException {
		if (this.jsonPrefix != null) {
			generator.writeRaw(this.jsonPrefix);
		}
	}

}
