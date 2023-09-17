package org.knzoon.painthelper.model;


import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class UniqueZone {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer takes;

    @Embedded
    private ZoneInfo zone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_takes_id")
    private RegionTakes regionTakes;

    public UniqueZone() {
    }

    public UniqueZone(Integer takes, ZoneInfo zone, RegionTakes regionTakes) {
        this.takes = takes;
        this.zone = zone;
        this.regionTakes = regionTakes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTakes() {
        return takes;
    }

    public void setTakes(Integer takes) {
        this.takes = takes;
    }

    public ZoneInfo getZone() {
        return zone;
    }

    public void setZone(ZoneInfo zone) {
        this.zone = zone;
    }

    public RegionTakes getRegionTakes() {
        return regionTakes;
    }

    public void setRegionTakes(RegionTakes regionTakes) {
        this.regionTakes = regionTakes;
    }

    public void addTake() {
        takes = takes + 1;
    }

    public void updateNameOrCoordinates(String name, Double latitude, Double longitude) {
        zone.setName(name);
        zone.setLatitude(latitude);
        zone.setLongitude(longitude);
    }


}
