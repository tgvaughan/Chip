/*
 * Copyright (c) 2024 Tim Vaughan
 *
 * This file is part of chip.
 *
 * chip is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * chip is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with chip. If not, see <https://www.gnu.org/licenses/>.
 */

package chip;

import beast.base.core.BEASTObject;
import beast.base.core.Input;
import beast.base.inference.Runnable;
import beast.pkgmgmt.BEASTClassLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ObjectInfo {
    String packageName;
    String className, classNameFQ;
    String description;
    String citations;

    Class<?> beastClass;

    List<InputInfo> inputInfos;

    boolean loadingError;


    /**
     * Create a new BOInfo object.  Involves lots of introspection
     * of the specified service in order to determine information
     * relevant to documentation.
     *
     * @param service fully qualified class name of the service.
     */
    public ObjectInfo(String service) {
        classNameFQ = service;
        className = service.substring(service.lastIndexOf('.')+1);
        packageName = service.substring(0, service.indexOf('.'));

        loadingError = false;

        try {
            beastClass = BEASTClassLoader.forName(
                    classNameFQ,
                    "beast.base.core.BEASTInterface");
            BEASTObject o = (BEASTObject) beastClass.newInstance();

            description = o.getDescription();
            citations = o.getCitations();

            inputInfos = new ArrayList<>();
            for (Field field : beastClass.getFields()) {
                if (!field.getType().isAssignableFrom(Input.class))
                    continue;

                inputInfos.add(new InputInfo(o, field));
            }

        } catch (NoClassDefFoundError | ClassNotFoundException | InstantiationException |
                 ClassCastException | IllegalAccessException e) {
            loadingError = true;
        }
    }

    public boolean isRunnable() {
        return Runnable.class.isAssignableFrom(beastClass);
    }

    public String toString() {
        return classNameFQ;
    }

}
