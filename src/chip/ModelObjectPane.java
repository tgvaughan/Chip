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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ModelObjectPane extends VBox {

    ObjectInfo objectInfo;

    public ModelObjectPane(ObjectInfo objectInfo) {
        this.objectInfo = objectInfo;

        HBox classNameBox = new HBox();
        Label className = new Label(objectInfo.className);
        classNameBox.getChildren().add(className);
        classNameBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(className, Priority.ALWAYS);

        classNameBox.setBorder(Border.stroke(Color.BLACK));
        getChildren().add(classNameBox);

        for (InputInfo inputInfo : objectInfo.inputInfos) {
            if (inputInfo.defaultValue != null) {
                getChildren().add(new Label(inputInfo.inputName));
            }
        }

        setBorder(Border.stroke(Color.BLACK));
        setAlignment(Pos.BASELINE_RIGHT);
        setBackground(Background.fill(Color.WHITE));

        setLayoutX(200);
        setLayoutY(200);
        makeDraggable();
    }

    double startDragX, startDragY;

    void makeDraggable() {
        setOnMousePressed(event -> {
            startDragX = getLayoutX()-event.getSceneX();
            startDragY = getLayoutY()-event.getSceneY();

            System.out.println(getLayoutX());
            System.out.println(event.getSceneX());
        });

        setOnMouseDragged(event -> {
            setLayoutX(startDragX + event.getSceneX());
            setLayoutY(startDragY + event.getSceneY());
        });
    }

}
