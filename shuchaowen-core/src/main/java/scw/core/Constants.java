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

package scw.core;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import scw.asm.Opcodes;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemUtils;
import scw.util.value.StringValue;
import scw.util.value.property.NotSupportEnumerationPropertyFactory;
import scw.util.value.property.PropertyFactory;

/**
 * This class can be used to parse other classes containing constant definitions
 * in public static final members. The {@code asXXXX} methods of this class
 * allow these constant values to be accessed via their string names.
 *
 * <p>
 * Consider class Foo containing {@code public final static int CONSTANT1 = 66;}
 * An instance of this class wrapping {@code Foo.class} will return the constant
 * value of 66 from its {@code asNumber} method given the argument
 * {@code "CONSTANT1"}.
 *
 * <p>
 * This class is ideal for use in PropertyEditors, enabling them to recognize
 * the same names as the constants themselves, and freeing them from maintaining
 * their own mapping.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16.03.2003
 */
public class Constants {
	private static final String CONSTANTS_KEY_PREFIX = "constants.";

	public static final String SYSTEM_PACKAGE_NAME = StringUtils.split(
			Constants.class.getPackage().getName(), '.')[0];

	public static final String DEFAULT_CHARSET_NAME = StringUtils.toString(
			GlobalPropertyFactory.getInstance().getString(
					"constants.default.charsetName"), "UTF-8");

	public static final Charset DEFAULT_CHARSET = Charset
			.forName(DEFAULT_CHARSET_NAME);

	/**
	 * The ASM version used internally throughout the framework.
	 *
	 * @see Opcodes#ASM4
	 */
	public static final int ASM_VERSION = StringUtils.parseInt(
			GlobalPropertyFactory.getInstance().getString(
					"constants.asm.version"), Opcodes.ASM5);

	/**
	 * 可用的处理器数量
	 */
	public static final int AVAILABLE_PROCESSORS = StringUtils.parseInt(
			GlobalPropertyFactory.getInstance().getString(
					"constants.available.processors"),
			SystemUtils.getAvailableProcessors());

	/**
	 * 注意：可能为空
	 */
	public static final String DEFAULT_PREFIX = GlobalPropertyFactory
			.getInstance().getString("constants.default.prefix");

	public static final Charset ISO_8859_1 = Charset.forName("iso-8859-1");

	public static final PropertyFactory PROPERTY_FACTORY = new NotSupportEnumerationPropertyFactory() {

		public scw.util.value.Value get(String key) {
			String k = key.toUpperCase();
			String value = null;
			if (k.startsWith(CONSTANTS_KEY_PREFIX.toUpperCase())) {
				k = k.substring(CONSTANTS_KEY_PREFIX.length());
				try {
					Field field = Constants.class.getField(k);
					Object v = field.get(null);
					value = v == null ? null : v.toString();
				} catch (Exception e) {
					// IGNORE
				}
			}
			return value == null ? GlobalPropertyFactory.getInstance().get(key)
					: new StringValue(value);
		};

		public java.util.Enumeration<String> enumerationKeys() {
			return GlobalPropertyFactory.getInstance().enumerationKeys();
		};
	};
}
