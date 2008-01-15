/*
 * Copyright 2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.eclipse.common.ui;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.seasar.eclipse.common.resource.LogUtil;

/**
 * 
 * @author taichi
 * 
 */
public class ImageLoader {

	public static void load(AbstractUIPlugin plugin, Class<?> holder) {
		load(plugin, holder, holder.getName());
	}

	public static void load(AbstractUIPlugin plugin, Class<?> holder,
			String name) {
		ResourceBundle rb = getBundle(name, holder.getClassLoader());
		if (rb == null) {
			return;
		}

		ImageRegistry registry = plugin.getImageRegistry();
		Field[] fields = holder.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (validateMask(field)) {
				continue;
			}
			String key = field.getName();
			String path = rb.getString(key);
			if (path == null || path.length() < 1) {
				log(plugin, key + " not found in " + name);
				continue;
			}
			ImageDescriptor id = registry.getDescriptor(key);
			if (id == null) {
				id = ImageDescriptor.createFromURL(FileLocator.find(plugin
						.getBundle(), new Path(path), null));
				registry.put(key, id);
			} else {
				log(plugin, key + " is already registered [" + holder + "]");
			}

			try {
				if (isAssignableFrom(ImageDescriptor.class, field)) {
					field.set(null, id);
				} else if (isAssignableFrom(Image.class, field)) {
					field.set(null, registry.get(key));
				}
			} catch (Exception e) {
				LogUtil.log(plugin, e);
			}
		}
	}

	public static void unload(Plugin plugin, Class<?> holder) {
		Field[] fields = holder.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			if (validateMask(field)) {
				continue;
			}
			try {
				field.set(null, null);
			} catch (Exception e) {
				LogUtil.log(plugin, e);
			}
		}
	}

	private static boolean validateMask(Field f) {
		final int MOD_EXPECTED = Modifier.PUBLIC | Modifier.STATIC;
		final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
		return (f.getModifiers() & MOD_MASK) != MOD_EXPECTED;
	}

	private static void log(Plugin p, String msg) {
		LogUtil.log(p, msg);
	}

	private static ResourceBundle getBundle(String name, ClassLoader loader) {
		try {
			return ResourceBundle.getBundle(name, Locale.getDefault(), loader);
		} catch (MissingResourceException e) {
			return null;
		}
	}

	private static boolean isAssignableFrom(final Class<?> clazz,
			final Field target) {
		return clazz.isAssignableFrom(target.getType());
	}

}
