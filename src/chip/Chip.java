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

import beast.base.inference.MCMC;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Chip extends Application {

    ObjectDatabase odb;

    private Parent createContent() {

        SplitPane splitPane = new SplitPane();

        ModelPane modelPane = new ModelPane();
        ObjectPane objectPane = new ObjectPane(odb);

        ModelObjectPane rootObjPane = new ModelObjectPane(odb.getInfo(MCMC.class));
        modelPane.addObject(rootObjPane);

        splitPane.getItems().addAll(
                modelPane,
                objectPane);
        splitPane.setDividerPositions(0.67);

        return splitPane;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        odb = new ObjectDatabase();
        primaryStage.setTitle("Chip");
        primaryStage.setScene(new Scene(createContent(), 640, 480));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
