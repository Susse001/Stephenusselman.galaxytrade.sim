package com.stephenu.gts.simulation;

import org.springframework.stereotype.Service;

import com.stephenu.gts.starsystem.StarSystem;

/**
 * Provides travel calculations used throughout the simulation.
 *
 * Travel time is determined by the distance between star systems and is
 * shared by trader movement and trade evaluation.
 */
@Service
public class TravelService {

    /**
     * Calculates the travel time between two star systems.
     *
     * Travel time is proportional to the Euclidean distance between systems
     * and is expressed as simulation ticks.
     *
     * @param from The origin system.
     * @param to The destination system.
     * @return The travel time in simulation ticks.
     */
    public int calculateTravelTicks(
            StarSystem from,
            StarSystem to) {

        double dx = from.getXCoordinate() - to.getXCoordinate();

        double dy = from.getYCoordinate() - to.getYCoordinate();

        double distance = Math.sqrt(dx * dx + dy * dy);

        return Math.max(
                1,
                (int) Math.ceil(distance / 7)
        );
    }
}
