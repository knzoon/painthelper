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
    private final static String USERNAME_SEARCHPATTERN = "/turf/user.php";
    private final static String DATA_START_SEARCHPATTERN = "\"type\": \"FeatureCollection\"";
    private final static String DATA_END_SEARCHPATTERN = "},";

    public WardedDataDTO parse(MultipartFile file) {
        try {
            String username = null;

            StringBuilder theActualData = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            boolean searchingForUsername = true;
            boolean searchingForStartOfData = false;
            boolean insideData = false;
            String line = bufferedReader.readLine();
            while (line != null) {

                if (searchingForUsername) {
                    int searchpatternFoundAtIndex = line.indexOf(USERNAME_SEARCHPATTERN);

                    if (searchpatternFoundAtIndex != -1) {
                        String afterSearchPattern = line.substring(searchpatternFoundAtIndex + USERNAME_SEARCHPATTERN.length());
                        int indexOfOpeningGt = afterSearchPattern.indexOf(">");
                        String usernameAndTheRest = afterSearchPattern.substring(indexOfOpeningGt + 1);
                        int indexOfClosingLt = usernameAndTheRest.indexOf("<");
                        username = usernameAndTheRest.substring(0, indexOfClosingLt);

                        searchingForUsername = false;
                        searchingForStartOfData = true;
                    }
                } else if (searchingForStartOfData) {
                    int searchpatternFoundAtIndex = line.indexOf(DATA_START_SEARCHPATTERN);

                    if (searchpatternFoundAtIndex != -1) {
                        theActualData.append("{\n");
                        theActualData.append(line);
                        searchingForStartOfData = false;
                        insideData = true;
                    }
                } else if (insideData) {

                    if (line.equals(DATA_END_SEARCHPATTERN)) {
                        theActualData.append("}");
                        insideData = false;
                    } else {
                        theActualData.append(line);
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
            UniqueWardedZones uniqueWardedZones = objectMapper.readValue(theActualData.toString(), UniqueWardedZones.class);

            return new WardedDataDTO(uniqueWardedZones, username);

        } catch (Exception e) {
            throw new ValidationException("Filen du försökt importera från innehåller fel", e);
        }
    }

    private boolean missingValues(String username, StringBuilder theActualData) {
        boolean noUserName = username == null;
        boolean noData = theActualData.length() == 0;

        return noUserName || noData;
    }
}
