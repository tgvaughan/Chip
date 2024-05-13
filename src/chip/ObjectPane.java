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

import beast.pkgmgmt.BEASTClassLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ObjectPane extends VBox {

    TreeItem<BOTreeEntry> objectTreeRoot, objectTreeRootCopy;

    ObjectDatabase odb;

    public ObjectPane(ObjectDatabase odb) {
        this.odb = odb;

        createTreeView();
    }

    public void createTreeView() {
        objectTreeRoot = new TreeItem<> (new BOTreeEntry("Installed Packages"));
        objectTreeRootCopy = new TreeItem<> (objectTreeRoot.getValue());

        for (String packageName : odb.beastObjectsByPackage.keySet()) {
            TreeItem<BOTreeEntry> packageItem = new TreeItem<> (new BOTreeEntry(packageName));
            TreeItem<BOTreeEntry> packageItemCopy = new TreeItem<>(packageItem.getValue());

            for (ObjectInfo boInfo : odb.beastObjectsByPackage.get(packageName)) {
                TreeItem<BOTreeEntry> boItem = new TreeItem<>(new BOTreeEntry(boInfo.classNameFQ, boInfo));
                packageItem.getChildren().add(boItem);
                packageItemCopy.getChildren().add(boItem);
            }

            packageItem.getValue().size = packageItem.getChildren().size();

            objectTreeRoot.getChildren().add(packageItem);
            objectTreeRootCopy.getChildren().add(packageItemCopy);
        }

        objectTreeRoot.getValue().size = objectTreeRoot.getChildren().size();
        objectTreeRoot.expandedProperty().setValue(true);

        TreeView<BOTreeEntry> treeView = new TreeView<> (objectTreeRoot);


        StackPane treePane = new StackPane();
        treePane.getChildren().add(treeView);

        getChildren().add(treePane);
        VBox.setVgrow(treePane, Priority.ALWAYS);


        HBox searchBox = new HBox();
        searchBox.getChildren().add(new Label("Filter:"));
        TextField searchField = new TextField();
        searchBox.getChildren().add(searchField);
        HBox.setHgrow(searchField, Priority.ALWAYS);


        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterObjectTree(newValue.trim().toLowerCase(), null);
        });

        getChildren().add(searchBox);
    }

    /**
     * Replaces the object tree with a tree containing
     * only those services containing a specific substring.
     */
    private void filterObjectTree(String searchString, String assignableToClassName) {

        // Extract assignable to class and search string from UI elements.
        // (It's ugly to have UI code polluting this method, but easy
        // alternatives were uglier!)

        if (assignableToClassName == null)
            assignableToClassName = "";
        else
            assignableToClassName = assignableToClassName.trim();

        if (assignableToClassName.isEmpty())
            assignableToClassName = "beast.base.core.BEASTInterface";
        Class<?> assignableToClass;
        try {
            assignableToClass = BEASTClassLoader.forName(assignableToClassName);
        } catch (ClassNotFoundException e) {
            return; // Abort filtering
        }

        // Do the filtering:

        objectTreeRoot.getChildren().clear();
        for (TreeItem<BOTreeEntry> packageItemCopy : objectTreeRootCopy.getChildren()) {
            TreeItem<BOTreeEntry> packageItem = new TreeItem<>(packageItemCopy.getValue());

            for (TreeItem<BOTreeEntry> objectItem : packageItemCopy.getChildren()) {
                if (objectItem.getValue().boInfo.classNameFQ.toLowerCase().contains(searchString)
                        && assignableToClass.isAssignableFrom(objectItem.getValue().boInfo.beastClass))
                    packageItem.getChildren().add(objectItem);
            }

            if (!packageItem.getChildren().isEmpty())
                objectTreeRoot.getChildren().add(packageItem);

            packageItem.getValue().size = packageItem.getChildren().size();
        }

        objectTreeRoot.getValue().size = objectTreeRoot.getChildren().size();
    }

    /**
     * Class of objects representing individual entries in the beast object
     * tree.  This is only necessary because I want to mix both BOInfo objects
     * and non-BOInfo objects (packages names, the tree root) in the same tree,
     * while also being able to retrieve the BOInfo objects for specific
     * tree elements when available.
     */
    public static class BOTreeEntry {
        String display;
        ObjectInfo boInfo;
        Integer size;

        public BOTreeEntry(String display, ObjectInfo boInfo) {
            this.display = display;
            this.boInfo = boInfo;
        }

        public BOTreeEntry(String display) {
            this(display, null);
        }

        @Override
        public String toString() {
            return display + (size == null ? "" : " (" + size + ")");
        }
    }
}
