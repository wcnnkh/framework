/*
 * Copyright 2003 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scw.core.cglib.core;

/**
 * Customize the generated class name for {@link AbstractClassGenerator}-based utilities.
 */

public interface NamingPolicy {
    /**
     * Choose a name for a generated class.
     * @param prefix a dotted-name chosen by the generating class (possibly to put the generated class in a particular package)
     * @param source the fully-qualified class name of the generating class (for example "scw.core.cglib.proxy.Enhancer")
     * @param key A key object representing the state of the parameters; for caching to work properly, equal keys should result
     * in the same generated class name. The default policy incorporates <code>key.hashCode()</code> into the class name.
     * @param names a predicate that returns true if the given classname has already been used in the same ClassLoader.
     * @return the fully-qualified class name
     */
    String getClassName(String prefix, String source, Object key, Predicate names);

    /**
     * The <code>NamingPolicy</code> in use does not currently, but may
     * in the future, affect the caching of classes generated by {@link
     * AbstractClassGenerator}, so this is a reminder that you should
     * correctly implement <code>equals</code> and <code>hashCode</code>
     * to avoid generating too many classes.
     */
    boolean equals(Object o);
}
