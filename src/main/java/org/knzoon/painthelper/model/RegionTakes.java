package org.knzoon.painthelper.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RegionTakes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String regionName;

    private Long regionId;

    private Long userId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "regionTakes")
    private List<UniqueZone> uniqueZones = new ArrayList<>();

    public RegionTakes() {
    }

    public RegionTakes(String regionName, Long regionId, Long userId) {
        this.regionName = regionName;
        this.regionId = regionId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<UniqueZone> getUniqueZones() {
        return uniqueZones;
    }

    public void setUniqueZones(List<UniqueZone> uniqueZones) {
        this.uniqueZones = uniqueZones;
    }
}
