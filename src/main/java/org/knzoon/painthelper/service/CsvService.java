package org.knzoon.painthelper.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.knzoon.painthelper.model.TakeoverNeoView;
import org.knzoon.painthelper.model.TakeoverRepository;
import org.knzoon.painthelper.model.TakeoverType;
import org.knzoon.painthelper.model.dto.NodeCsvDTO;
import org.knzoon.painthelper.model.dto.RelationCsvDTO;
import org.knzoon.painthelper.util.RoundCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CsvService {

    private final TakeoverRepository takeoverRepository;
    private final PointsCalculator pointsCalculator;


    private Logger logger = LoggerFactory.getLogger(CsvService.class);

    public CsvService(TakeoverRepository takeoverRepository, PointsCalculator pointsCalculator) {
        this.takeoverRepository = takeoverRepository;
        this.pointsCalculator = pointsCalculator;
    }

    public ByteArrayInputStream getCsvForNodes(Integer roundId) {

        Long areaId = 2064L;

        List<TakeoverNeoView> takeovers = takeoverRepository.findAllByRoundIdAndAreaId(roundId, areaId);
        List<NodeCsvDTO> nodes = transformToNodes(takeovers, roundId);

        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            List<String> header = Arrays.asList("ZoneId", "Name", "Points", "Latitude", "Longitude");
            csvPrinter.printRecord(header);

            for (NodeCsvDTO node : nodes) {
                List<String> data = Arrays.asList(
                        node.getZoneId(),
                        node.getZoneName(),
                        node.getPoints(),
                        node.getLatitude(),
                        node.getLongitude()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }

    private List<NodeCsvDTO> transformToNodes(List<TakeoverNeoView> takeovers, Integer roundId) {
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));
        return takeovers.stream().map(t -> this.toNodeCsv(t, roundId, now)).collect(Collectors.toList());
    }

    private NodeCsvDTO toNodeCsv(TakeoverNeoView takeover, Integer roundId, ZonedDateTime now) {
        Integer points = calculatePoints(takeover, roundId, now);
        return new NodeCsvDTO(takeover.getZoneId(), takeover.getZoneName(), points, takeover.getLatitude(), takeover.getLongitude());
    }

    private Integer calculatePoints(TakeoverNeoView takeover, Integer roundId, ZonedDateTime now) {
        if (TakeoverType.ASSIST.getType().equals(takeover.getType())) {
            return takeover.getTp();
        }

        ZonedDateTime endTime = takeover.getLostTimeConverted();

        if (endTime == null) {
            endTime = RoundCalculator.calculateEndTime(roundId, now);
        }

        return pointsCalculator.pointsForTakeover(takeover, endTime);
    }

    public ByteArrayInputStream getCsvForRelations(Integer roundId) {

        Long areaId = 2064L;

        List<TakeoverNeoView> takeovers = takeoverRepository.findAllByRoundIdAndAreaId(roundId, areaId);
        List<RelationCsvDTO> relations = transformToRelations(takeovers);

        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            List<String> header = Arrays.asList("FromZoneId", "ToZoneId", "Time");
            csvPrinter.printRecord(header);

            for (RelationCsvDTO relation : relations) {
                List<String> data = Arrays.asList(
                        relation.getFromZoneId(),
                        relation.getToZoneId(),
                        relation.getTimeInSeconds()
                );
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }

    private List<RelationCsvDTO> transformToRelations(List<TakeoverNeoView> takeovers) {
        List<RelationCsvDTO> relations = new ArrayList<>();
        Map<Long, List<TakeoverNeoView>> takeoversPerUserMap = takeovers.stream().collect(Collectors.groupingBy(TakeoverNeoView::getUserId));
        takeoversPerUserMap.values().forEach(takeoverList -> this.addRelationsForUser(takeoverList, relations));

        return relations;
    }

    private void addRelationsForUser(List<TakeoverNeoView> takeoverList, List<RelationCsvDTO> relations) {
        Collections.sort(takeoverList, Comparator.comparing(TakeoverNeoView::getTakeoverTimeConverted));
        TakeoverNeoView prevTakeover = null;

        for (TakeoverNeoView currTakeover : takeoverList) {
            if (prevTakeover != null) {
                long secondsBetweenTakes = Duration.between(prevTakeover.getTakeoverTimeConverted(), currTakeover.getTakeoverTimeConverted()).getSeconds();

                if (secondsBetweenTakes < 3600) {
                    if (secondsBetweenTakes < 30) {
                        logger.info("Extremt snabb resa {}s mellan zonerna {} och {}", secondsBetweenTakes, prevTakeover.getZoneName(), currTakeover.getZoneName());
                        logger.info("Frånzon: {}, {}, {}, {}", prevTakeover.getUserId(), prevTakeover.getZoneId(), prevTakeover.getZoneName(), prevTakeover.getTakeoverTimeConverted());
                        logger.info("Tillzon: {}, {}, {}, {}", currTakeover.getUserId(), currTakeover.getZoneId(), currTakeover.getZoneName(), currTakeover.getTakeoverTimeConverted());
                    } else {
                        relations.add(new RelationCsvDTO(prevTakeover.getZoneId(), currTakeover.getZoneId(), (int) secondsBetweenTakes));
                    }
                }
            }
            prevTakeover = currTakeover;
        }

    }

}
