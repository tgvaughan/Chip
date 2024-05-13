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

import beast.pkgmgmt.PackageManager;

import java.io.IOException;
import java.util.*;

public class ObjectDatabase {

    /**
     * Map from package names (really just the root package of the service
     * FQCNs) to lists containing the processed BOInfo objects.
     */
    public SortedMap<String, List<ObjectInfo>> beastObjectsByPackage;
    public List<ObjectInfo> allBeastObjects;
    public List<String> allBeastObjectNames;

    /**
     * Populate the beastObjects map.
     * @throws IOException thrown by PackageManager.loadExternalJars() when
     *                     something goes wrong.
     */
    public ObjectDatabase() throws IOException {
        beastObjectsByPackage = new TreeMap<>();
        allBeastObjects = new ArrayList<>();
        allBeastObjectNames = new ArrayList<>();

        PackageManager.loadExternalJars();

        for (String service : PackageManager.listServices("beast.base.core.BEASTInterface")) {
            ObjectInfo boInfo = new ObjectInfo(service);
            if (boInfo.loadingError)
                continue;

            if (!beastObjectsByPackage.containsKey(boInfo.packageName))
                beastObjectsByPackage.put(boInfo.packageName, new ArrayList<>());

            beastObjectsByPackage.get(boInfo.packageName).add(boInfo);
            allBeastObjects.add(boInfo);
            allBeastObjectNames.add(boInfo.classNameFQ);
        }

        // Add some special BEASTObjects: (Is there an easy way to populate this list automatically?)
        allBeastObjectNames.add("beast.base.core.Function");
        allBeastObjectNames.add("beast.base.evolution.TreeInterface");
        allBeastObjectNames.add("beast.base.inference.StateNode");
        allBeastObjectNames.add("beast.base.inference.CalculationNode");
        allBeastObjectNames.add("beast.base.inference.Distribution");
        allBeastObjectNames.add("beast.base.inference.distribution.ParametricDistribution");
        allBeastObjectNames.add("beast.base.inference.StateNodeInitialiser");
        allBeastObjectNames.add("beast.base.evolution.substitionmodel.SubtitutionModel");
        allBeastObjectNames.add("beast.base.evolution.branchratemodel.BranchRateModel");

        // Ensure objects are sorted lexicographically
        for (String packageName : beastObjectsByPackage.keySet()) {
            beastObjectsByPackage.get(packageName).sort(Comparator.comparing(o -> o.classNameFQ));
        }
        allBeastObjectNames.sort(String::compareTo);
    }

    public ObjectInfo getInfo(Class<?> c) {
        return allBeastObjects.stream().filter(bo -> bo.beastClass==c).findAny().orElse(null);
    }
}
