package org.knzoon.painthelper.controller;


import org.knzoon.painthelper.model.ValidationException;
import org.knzoon.painthelper.model.dto.ImportResultWardedDTO;
import org.knzoon.painthelper.model.dto.WardedDataDTO;
import org.knzoon.painthelper.representation.ImportResultRepresentation;
import org.knzoon.painthelper.service.WardedRegionDataParser;
import org.knzoon.painthelper.service.ZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.net.URISyntaxException;
import java.util.List;

@RestController
public class FileUploadController {

    private final WardedRegionDataParser wardedRegionDataParser;
    private final ZoneService zoneService;

    private Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    public FileUploadController(WardedRegionDataParser wardedRegionDataParser, ZoneService zoneService) {
        this.wardedRegionDataParser = wardedRegionDataParser;
        this.zoneService = zoneService;
    }

    @CrossOrigin(origins = {"https://painthelper.knzoon.se"})
    @PostMapping("/api/upload")
    public ImportResultRepresentation uploadDataForRegion(@RequestParam("file") MultipartFile file) throws URISyntaxException {
        try {
            logger.info("File that is being uploaded and parsed: {}", file.getOriginalFilename());
            WardedDataDTO wardedDataDTO = wardedRegionDataParser.parseFile(file);
            List<ImportResultWardedDTO> importResults = zoneService.importZones(wardedDataDTO);

            String importInfo = createInfoString(importResults);
            int nrofImportedZones = calculateNrofImported(importResults);

            logger.info("Imported {} zones", nrofImportedZones);

            return new ImportResultRepresentation(importInfo, nrofImportedZones);
        }catch (ValidationException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    private String createInfoString(List<ImportResultWardedDTO> importResults) {
        String createdString = "Skapade följande regioner: ";
        final int origLengthCreated = createdString.length();
        String updatedString = "Uppdaterade följande regioner: ";
        final int origLengthUpdated = updatedString.length();

        for (ImportResultWardedDTO importResult : importResults) {
            if (importResult.isUpdate()) {
                updatedString += importResult.getRegionName() + ", ";
            } else {
                createdString += importResult.getRegionName() + ", ";
            }
        }

        String totalString = "";

        if (createdString.length() != origLengthCreated) {
            totalString += createdString;
        }

        if (updatedString.length() != origLengthUpdated) {
            totalString += updatedString;
        }

        return totalString;
    }

    private int calculateNrofImported(List<ImportResultWardedDTO> importResults) {
        int tot = 0;

        for (ImportResultWardedDTO importResult : importResults) {
            tot += importResult.getNrofImported();
        }

        return tot;
    }

}
