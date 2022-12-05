package nl.tudelft.simulation.supplychain.dsol;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.point.Point;

import nl.tudelft.simulation.dsol.model.DSOLModel;

/**
 * SCModelInterface defines the extra methods of a supply chain model.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface SCModelInterface extends DSOLModel<Duration, SCSimulatorInterface>
{
    /**
     * Calculate the distance between two points as a Length. The point could be in lat/lon, on an orthogonal grid, or based on
     * a GIS projection.
     * @param loc1 Point; first location
     * @param loc2 Point; second location
     * @return Length; distance between the locations
     */
    Length calculateDistance(Point<?> loc1, Point<?> loc2);

    /**
     * Calculate the distance between two points in km. The point could be in lat/lon, on an orthogonal grid, or based on a GIS
     * projection.
     * @param loc1 Point; first location
     * @param loc2 Point; second location
     * @return double; distance between the locations in km
     */
    default double calculateDistanceKm(final Point<?> loc1, final Point<?> loc2)
    {
        return calculateDistance(loc1, loc2).si / 1000.0;
    }

}
