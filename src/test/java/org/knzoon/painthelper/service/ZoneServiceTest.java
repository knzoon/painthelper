package org.knzoon.painthelper.service;


import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ZoneServiceTest {

    @Test
    void testArne() {
        Double a = Double.valueOf("63.807757");
        Double b = Double.valueOf("63.812622");
        Double c = Double.valueOf("63.804923");
        Double d = Double.valueOf("63.812352");
        Double e = Double.valueOf("63.810801");
        Double f = Double.valueOf("63.808958");
        Double g = Double.valueOf("63.813978");
        List<Double> doubles = Arrays.asList(a, b, c, d, e, f, g);

        Double maxDouble = doubles.stream().max(Double::compareTo).get();
        assertThat(maxDouble).isEqualTo(g);

        Double minDouble = doubles.stream().min(Double::compareTo).get();
        assertThat(minDouble).isEqualTo(c);

        Double avg = (maxDouble + minDouble) / 2;
        assertThat(avg).isEqualTo(Double.valueOf(63.8094505));
    }

}