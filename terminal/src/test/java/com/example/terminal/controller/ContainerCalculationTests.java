package com.example.terminal.controller;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ContainerCalculationTests {

    @Test
    void shortDwellIsDisplayedInSecondsWithNoDemurrage() {
        Duration dwell = Duration.ofSeconds(26);

        assertEquals("26 sec", ContainerController.formatDwellTime(dwell));
        assertEquals(0, ContainerController.calculateDemurrage(dwell));
    }

    @Test
    void oneMinuteAndFortyFiveSecondsIsDisplayedAccurately() {
        assertEquals("1 min 45 sec", ContainerController.formatDwellTime(Duration.ofSeconds(105)));
    }

    @Test
    void fiveHoursThirtyMinutesAndSecondsDisplayWithoutAffectingCalculation() {
        Duration dwell = Duration.ofHours(5).plusMinutes(30).plusSeconds(32);

        assertEquals("5 hrs 30 min", ContainerController.formatDwellTime(dwell));
        assertEquals(0, ContainerController.calculateDemurrage(dwell));
    }

    @Test
    void twentyFourHourDwellHasNoDemurrage() {
        Duration dwell = Duration.ofHours(24);

        assertEquals("1 day", ContainerController.formatDwellTime(dwell));
        assertEquals(0, ContainerController.calculateDemurrage(dwell));
    }

    @Test
    void seventyTwoHourDwellChargesOnlyTwentyFourExcessHours() {
        Duration dwell = Duration.ofHours(72);

        assertEquals("3 days", ContainerController.formatDwellTime(dwell));
        assertEquals(2400.0, ContainerController.calculateDemurrage(dwell));
    }

    @Test
    void twoDaysFiveHoursAndThirtyMinutesUseDayBasedDisplay() {
        assertEquals("2 days 5 hrs 30 min",
                ContainerController.formatDwellTime(Duration.ofDays(2).plusHours(5).plusMinutes(30)));
    }
}