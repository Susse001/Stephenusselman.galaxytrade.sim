package com.stephenu.gts.starsystem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.stephenu.gts.planet.Planet;

class StarSystemTest {

    @Test
    void shouldCreateStarSystemWithProvidedValues() {

        StarSystem system = new StarSystem(
                1L,
                "Orion",
                25,
                40,
                Region.INNER_RIM
        );

        assertEquals(1L, system.getId());
        assertEquals("Orion", system.getName());
        assertEquals(25, system.getXCoordinate());
        assertEquals(40, system.getYCoordinate());
        assertEquals(Region.INNER_RIM, system.getRegion());
    }

    @Test
    void shouldStartWithEmptyPlanetList() {

        StarSystem system = new StarSystem();

        assertNotNull(system.getPlanets());
        assertTrue(system.getPlanets().isEmpty());
    }

    @Test
    void addPlanetShouldAddPlanetToSystem() {

        StarSystem system = new StarSystem();
        Planet planet = new Planet();

        system.addPlanet(planet);

        assertEquals(1, system.getPlanets().size());
        assertTrue(system.getPlanets().contains(planet));
    }

    @Test
    void addPlanetShouldSetStarSystemOnPlanet() {

        StarSystem system = new StarSystem();
        Planet planet = new Planet();

        system.addPlanet(planet);

        assertEquals(system, planet.getStarSystem());
    }

    @Test
    void addPlanetShouldMaintainRelationshipForMultiplePlanets() {

        StarSystem system = new StarSystem();

        Planet firstPlanet = new Planet();
        Planet secondPlanet = new Planet();
        Planet thirdPlanet = new Planet();

        system.addPlanet(firstPlanet);
        system.addPlanet(secondPlanet);
        system.addPlanet(thirdPlanet);

        assertEquals(3, system.getPlanets().size());

        assertEquals(system, firstPlanet.getStarSystem());
        assertEquals(system, secondPlanet.getStarSystem());
        assertEquals(system, thirdPlanet.getStarSystem());
    }

    @Test
    void removePlanetShouldRemovePlanetFromSystem() {

        StarSystem system = new StarSystem();
        Planet planet = new Planet();

        system.addPlanet(planet);
        system.removePlanet(planet);

        assertTrue(system.getPlanets().isEmpty());
        assertFalse(system.getPlanets().contains(planet));
    }

    @Test
    void removePlanetShouldClearStarSystemOnPlanet() {

        StarSystem system = new StarSystem();
        Planet planet = new Planet();

        system.addPlanet(planet);
        system.removePlanet(planet);

        assertNull(planet.getStarSystem());
    }

    @Test
    void removePlanetShouldNotAffectOtherPlanets() {

        StarSystem system = new StarSystem();

        Planet firstPlanet = new Planet();
        Planet secondPlanet = new Planet();
        Planet thirdPlanet = new Planet();

        system.addPlanet(firstPlanet);
        system.addPlanet(secondPlanet);
        system.addPlanet(thirdPlanet);

        system.removePlanet(secondPlanet);

        assertEquals(2, system.getPlanets().size());

        assertTrue(system.getPlanets().contains(firstPlanet));
        assertFalse(system.getPlanets().contains(secondPlanet));
        assertTrue(system.getPlanets().contains(thirdPlanet));

        assertEquals(system, firstPlanet.getStarSystem());
        assertNull(secondPlanet.getStarSystem());
        assertEquals(system, thirdPlanet.getStarSystem());
    }
}