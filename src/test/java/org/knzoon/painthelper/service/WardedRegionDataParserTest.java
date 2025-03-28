package org.knzoon.painthelper.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.representation.warded.UniqueWardedZones;

import static org.assertj.core.api.Assertions.assertThat;

class WardedRegionDataParserTest {
    private final static String DATA_START_SEARCHPATTERN = "\"type\":\"FeatureCollection\"";

    private final static String LINE_CONTAINING_DATA = "                    data: {\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[18.702778,64.598033]},\"properties\":{\"title\":\"Norrmalmzon\",\"count\":1,\"zone_type\":null}}]}";


    @Test
    public void testArne() {
        final String REGION_SEARCHPATTERN = "<option selected=\"\" value=\"";

        String apaEtt = "<option value=\"0\">Global</option>";
        String apaTvå = "<option value=\"144\">Gävleborg</option><option value=\"141\">Stockholm</option><option value=\"142\">Uppsala</option><option SELECTED=\"\" value=\"127\">Västerbotten</option><option value=\"145\">Västernorrland</option><option value=\"143\">Västmanland</option><option value=\"132\">Västra Götaland</option><option value=\"131\">Örebro</option><option value=\"216\">Czech Republic</option><option value=\"211\">Spain</option><option value=\"220\">Thailand</option><option value=\"186\">Länsi- ja Sisä-Suomi</option></select>";
        String apaTre = "    });";

        int iDidntFind = apaEtt.toLowerCase().lastIndexOf(REGION_SEARCHPATTERN);
        int iFound = apaTvå.toLowerCase().lastIndexOf(REGION_SEARCHPATTERN);
        String idAndTheRest = apaTvå.substring(iFound + REGION_SEARCHPATTERN.length());
        int iOfClosingFnutt = idAndTheRest.indexOf("\"");
        String idValue = idAndTheRest.substring(0, iOfClosingFnutt);
        assertThat(idValue).isEqualTo("127");
        int iOfStartOfOption = idAndTheRest.indexOf("<");
        String regionName = idAndTheRest.substring(iOfClosingFnutt + 2, iOfStartOfOption);
        assertThat(regionName).isEqualTo("Västerbotten");

        iFound = apaTre.indexOf("});");
        String endOfData = apaTre.substring(0, iFound + 1);
        assertThat(endOfData).isEqualTo("    }");
    }

    @Test
    public void canParseUsername() {
        WardedRegionDataParser parser = new WardedRegionDataParser();
        String username = parser.parseUsername(8, "        <button class=\"dropbtn\">praktikus            <i class=\"dropbtnarrow\"></i>");
        assertThat(username).isEqualTo("praktikus");
    }

    @Test
    public void canFindStartOfDataAndIsolateData() {

        int searchpatternFoundAtIndex = LINE_CONTAINING_DATA.indexOf(DATA_START_SEARCHPATTERN);
        assertThat(searchpatternFoundAtIndex).isPositive();

    }

    @Test
    public void canParseDataAndConvertFromJson() throws Exception{
        WardedRegionDataParser parser = new WardedRegionDataParser();
        String jsonData = parser.parseActualData(LINE_CONTAINING_DATA);
        ObjectMapper objectMapper = new ObjectMapper();
        UniqueWardedZones uniqueWardedZones = objectMapper.readValue(jsonData, UniqueWardedZones.class);

        assertThat(uniqueWardedZones).isNotNull();
    }

}