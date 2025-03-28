package org.knzoon.painthelper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.knzoon.painthelper.model.ValidationException;
import org.knzoon.painthelper.model.dto.WardedDataDTO;
import org.knzoon.painthelper.representation.warded.UniqueWardedZones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class WardedRegionDataParser {

    Logger logger = LoggerFactory.getLogger(WardedRegionDataParser.class);
    private final static String USERNAME_SEARCHPATTERN = "<button class=\"dropbtn\">";
    private final static String DATA_START_SEARCHPATTERN = "\"type\":\"FeatureCollection\"";
    private final static String PRE_ACTUAL_DATA = "data: ";

    public WardedDataDTO parse(MultipartFile file) {
        try {
            String username = null;

            String theActualData = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            boolean searchingForUsername = true;
            boolean searchingForStartOfData = false;
            String line = bufferedReader.readLine();
            while (line != null) {

                if (searchingForUsername) {
                    int searchpatternFoundAtIndex = line.indexOf(USERNAME_SEARCHPATTERN);

                    if (searchpatternFoundAtIndex != -1) {
                        username = parseUsername(searchpatternFoundAtIndex, line);

                        logger.info("found username {}", username);
                        searchingForUsername = false;
                        searchingForStartOfData = true;
                    }
                } else if (searchingForStartOfData) {
                    int searchpatternFoundAtIndex = line.indexOf(DATA_START_SEARCHPATTERN);

                    if (searchpatternFoundAtIndex != -1) {
                        logger.info("found start of data");
                        theActualData = parseActualData(line);
                        searchingForStartOfData = false;
                    }
                } else {
                    // throw away the rest
                }

                line = bufferedReader.readLine();
            }

            logger.info("Parsing of file completed");

            if (missingValues(username, theActualData)) {
                logger.error("Parse failed");
                throw new ValidationException("Filen du försökt importera från innehåller fel");
            }
            logger.info("Current username: {}", username);

            ObjectMapper objectMapper = new ObjectMapper();
            UniqueWardedZones uniqueWardedZones = objectMapper.readValue(theActualData, UniqueWardedZones.class);

            return new WardedDataDTO(uniqueWardedZones, username);

        } catch (Exception e) {
            throw new ValidationException("Filen du försökt importera från innehåller fel", e);
        }
    }

    String parseUsername(int searchpatternFoundAtIndex, String line) {
        String afterSearchPattern = line.substring(searchpatternFoundAtIndex + USERNAME_SEARCHPATTERN.length());
        int indexOfClosingLt = afterSearchPattern.indexOf("<");
        String usernameUntrimmed = afterSearchPattern.substring(0, indexOfClosingLt);
        return usernameUntrimmed.trim();
    }

    String parseActualData(String line) {
        return line.substring(line.indexOf(PRE_ACTUAL_DATA) + PRE_ACTUAL_DATA.length());
    }


    private boolean missingValues(String username, String theActualData) {
        boolean noUserName = username == null;
        boolean noData = theActualData.isEmpty();

        return noUserName || noData;
    }
}
