package org.knzoon.painthelper.controller;

import org.knzoon.painthelper.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CsvExportController {

    private final CsvService csvService;

    @Autowired
    public CsvExportController(CsvService csvService) {
        this.csvService = csvService;
    }

    @GetMapping("/api/exportNodes")
    public ResponseEntity<Resource> getNodesAsCsv(@RequestParam(value = "roundId", defaultValue = "146") Integer roundId) {
        String filename = "nodes.csv";
        InputStreamResource file = new InputStreamResource(csvService.getCsvForNodes(roundId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @GetMapping("/api/exportRelations")
    public ResponseEntity<Resource> getRelationsAsCsv(@RequestParam(value = "roundId", defaultValue = "146") Integer roundId) {
        String filename = "relations.csv";
        InputStreamResource file = new InputStreamResource(csvService.getCsvForRelations(roundId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

}
