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

import javafx.scene.layout.Pane;

public class ModelPane extends Pane {

    public ModelPane() {
    }

    public ModelPane addObject(ModelObjectPane moPane) {
        getChildren().add(moPane);
        return this;
    }

}
