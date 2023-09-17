package org.knzoon.painthelper.representation;

public class ImportResultRepresentation {
    private final String importType;
    private final Integer nrofImported;

    public ImportResultRepresentation(String importType, int nrofImported) {
        this.importType = importType;
        this.nrofImported = nrofImported;
    }

    public String getImportType() {
        return importType;
    }

    public Integer getNrofImported() {
        return nrofImported;
    }
}
