package org.knzoon.painthelper.service;

import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.TakeoverTestbuilder;
import org.knzoon.painthelper.representation.compare.PphDistributionRepresentation;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PphDistributionFactoryTest {
    @Test
    public void canCreatePphDistribution() {
        Takeover takeoverWithPphOne = TakeoverTestbuilder.builder().withPph(1).build();
        Takeover takeoverWithPphSix = TakeoverTestbuilder.builder().withPph(6).build();
        Takeover takeoverWithPphNine = TakeoverTestbuilder.builder().withPph(9).build();
        Takeover anotherTakeoverWithPphNine = TakeoverTestbuilder.builder().withPph(9).build();
        List<Takeover> takeovers = Arrays.asList(takeoverWithPphOne, takeoverWithPphSix, takeoverWithPphNine, anotherTakeoverWithPphNine);
        PphDistributionRepresentation pphDistribution = PphDistributionFactory.create(takeovers);
        assertThat(pphDistribution).isNotNull();
        assertThat(pphDistribution.getPlusOne()).isEqualTo(1);
        assertThat(pphDistribution.getPlusNine()).isEqualTo(2);
        assertThat(pphDistribution.getPlusEight()).isEqualTo(0);
    }

}