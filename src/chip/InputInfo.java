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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class InputInfo {
    String inputName;
    String tipText;
    String inputClassNameFQ;
    String inputClassName;
    Boolean isList;
    Boolean required;
    Object defaultValue;


    List<ObjectInfo> assignableFrom;

    /**
     * Create a new InputInfo object.  Both the BEASTObject associated with
     * the input as well as the field corresponding to the actual input
     * are required in order to extract all of the documentation-relevant
     * information.
     *
     * @param beastObject BEASTObject possessing the input
     * @param inputField field corresponding to the input
     * @throws IllegalAccessException
     */
    public InputInfo(BEASTObject beastObject, Field inputField) throws IllegalAccessException {
        Input<?> input = (Input<?>) inputField.get(beastObject);

        inputName = input.getName();
        tipText = input.getTipText();

        try {
            // Awful do-si-do required to extract type of input.
            Type[] types = ((ParameterizedType) inputField.getGenericType()).getActualTypeArguments();
            Class<?> inputClass;

            isList = input.get() instanceof List;

            if (isList)
                inputClass = (Class<?>) ((ParameterizedType) types[0]).getActualTypeArguments()[0];
            else
                inputClass = (Class<?>) types[0];

            inputClassNameFQ = inputClass.getName();
            if (inputClassNameFQ.contains("$"))
                inputClassNameFQ = inputClassNameFQ.substring(0, inputClassNameFQ.lastIndexOf('$'));
            inputClassName = inputClassNameFQ.substring(inputClass.getName().lastIndexOf('.') + 1);

        } catch (ClassCastException e) {
            // Nothing to do.
        }

        required = input.getRule().equals(Input.Validate.REQUIRED);
        defaultValue = input.defaultValue;
    }
}
