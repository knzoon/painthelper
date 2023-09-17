package org.knzoon.painthelper.model.dto;

public class ImportResultWardedDTO {
    private final boolean regionTakesExists;

    private final String regionName;

    private final int nrofImported;

    public ImportResultWardedDTO(boolean regionTakesExists, String regionName, int nrofImported) {
        this.nrofImported = nrofImported;
        this.regionTakesExists = regionTakesExists;
        this.regionName = regionName;
    }

    public boolean isUpdate() {
        return regionTakesExists;
    }

    public String getAction() {
        if (regionTakesExists) {
            return "Uppdaterar";
        } else {
            return "Skapar";
        }
    }

    public String getRegionName() {
        return regionName;
    }

    public int getNrofImported() {
        return nrofImported;
    }
}
