package org.knzoon.painthelper.representation.warded;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UniqueWardedZones {

    private String type;
    private List<WardedZone> features;

    public UniqueWardedZones() {
    }

    public UniqueWardedZones(String type, List<WardedZone> zones) {
        this.type = type;
        this.features = zones;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<WardedZone> getFeatures() {
        return features;
    }

    public void setFeatures(List<WardedZone> features) {
        this.features = features;
    }

    public List<String> zoneNames() {
        return features.stream().map(WardedZone::zoneName).collect(Collectors.toList());
    }

    public int size() {
        return features.size();
    }

    public Map<String, WardedZone> toMap() {
        return features.stream().collect(Collectors.toMap(WardedZone::zoneName, Function.identity()));
    }

    public List<List<WardedZone>> zonesPartitioned() {
        int partitionSize = 400;

        List<List<WardedZone>> partitions = new ArrayList<>();

        for (int i = 0; i < features.size(); i += partitionSize) {
            partitions.add(features.subList(i, Math.min(i + partitionSize, features.size())));
        }

        return partitions;
    }
}
