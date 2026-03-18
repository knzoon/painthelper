package org.knzoon.painthelper.service;

import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.TakeoverTestbuilder;
import org.knzoon.painthelper.representation.compare.PphDistributionRepresentation;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PphDistributionFactoryTest {
    private static final Long ZONE_ONE = 1000L;
    private static final Long ZONE_TWO = 2000L;
    private static final Long ZONE_THREE = 3000L;

    @Test
    public void canCreatePphDistribution() {
        Takeover takeoverWithPphOne = TakeoverTestbuilder.builder().withPph(1).build();
        Takeover takeoverWithPphSix = TakeoverTestbuilder.builder().withZoneId(ZONE_ONE).withPph(6).build();
        Takeover anotherTakeoverWithPphSixOnAnotherZone = TakeoverTestbuilder.builder().withZoneId(ZONE_TWO).withPph(6).build();
        Takeover takeoverWithPphNine = TakeoverTestbuilder.builder().withZoneId(ZONE_THREE).withPph(9).build();
        Takeover anotherTakeoverWithPphNine = TakeoverTestbuilder.builder().withZoneId(ZONE_THREE).withPph(9).build();
        List<Takeover> takeovers = Arrays.asList(
                takeoverWithPphOne,
                takeoverWithPphSix,
                anotherTakeoverWithPphSixOnAnotherZone,
                takeoverWithPphNine,
                anotherTakeoverWithPphNine);
        PphDistributionRepresentation pphDistribution = PphDistributionFactory.create(takeovers);
        assertThat(pphDistribution).isNotNull();
        assertThat(pphDistribution.getPlusOne()).isEqualTo(1);
        assertThat(pphDistribution.getPlusSix()).isEqualTo(2);
        assertThat(pphDistribution.getPlusNine()).isEqualTo(1);
        assertThat(pphDistribution.getPlusEight()).isEqualTo(0);
    }

}