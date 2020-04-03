/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.core.type;

import org.springframework.lang.Nullable;

/**
 * Interface that defines abstract metadata of a specific class,
 * in a form that does not require that class to be loaded yet.
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see StandardClassMetadata
 * @see org.springframework.core.type.classreading.MetadataReader#getClassMetadata()
 * @see AnnotationMetadata
 */
public interface ClassMetadata {

	/**
	 * Return the name of the underlying class.
	 */
	String getClassName();

	/**
	 * Return whether the underlying class represents an interface.
	 */
	boolean isInterface();

	/**
	 * Return whether the underlying class represents an annotation.
	 * @since 4.1
	 */
	boolean isAnnotation();

	/**
	 * 返回所描述的类是否被标记为抽象.
	 */
	boolean isAbstract();

	/**
	 * Return whether the underlying class represents a concrete class,
	 * i.e. neither an interface nor an abstract class.
	 *
	 * 返回所描述的类是否表示具体类,即这个类是不是既不是接口,也不是抽象类.
	 *
	 */
	boolean isConcrete();

	/**
	 * Return whether the underlying class is marked as 'final'.
	 */
	boolean isFinal();

	/**
	 * Determine whether the underlying class is independent, i.e. whether
	 * it is a top-level class or a nested class (static inner class) that
	 * can be constructed independently from an enclosing class.
	 *
	 * 确定所描述的类是否是独立的,即它是顶级类还是静态内部类.
	 *
	 */
	boolean isIndependent();

	/**
	 * 返回基础类是否是一个嵌套类类(即,底层类是一个内部或者嵌套类,还是某个方法中的局部类).
	 * <p>如果这个方法返回{@code false},这意味着底层类是一个共有的顶级类.
	 * <p>
	 * 通常,用来判断添加了注解A的类是否是一个public class 修饰的.
	 * 如果添加了注解A的类是一个内部类,嵌套类,或者是在方法内部定义的匿名内部类,则,此方法返回{@code false}.
	 */
	boolean hasEnclosingClass();

	/**
	 * {@link #hasEnclosingClass()}的返回值代表添加了注解A的类是否是一个顶级类,
	 * 如果不是顶级类,则{@link #hasEnclosingClass()}返回{@code false},
	 * 那么返回{@code false}以后,后面可能还需要那个类的全类名啊.
	 * 所以,这个方法就是提供返回全类名的.不过,{@link #hasEnclosingClass()}
	 * 返回的是{@code true},那么调用该函数则返回{@code null},
	 * 否则,返回该内部类,嵌套类或者方法内部定义的类的全类名
	 */
	@Nullable
	String getEnclosingClassName();

	/**
	 * Return whether the underlying class has a super class.
	 */
	boolean hasSuperClass();

	/**
	 * Return the name of the super class of the underlying class,
	 * or {@code null} if there is no super class defined.
	 */
	@Nullable
	String getSuperClassName();

	/**
	 * Return the names of all interfaces that the underlying class
	 * implements, or an empty array if there are none.
	 */
	String[] getInterfaceNames();

	/**
	 * Return the names of all classes declared as members of the class represented by
	 * this ClassMetadata object. This includes public, protected, default (package)
	 * access, and private classes and interfaces declared by the class, but excludes
	 * inherited classes and interfaces. An empty array is returned if no member classes
	 * or interfaces exist.
	 * @since 3.1
	 */
	String[] getMemberClassNames();

}
