package nl.tudelft.simulation.supplychain.product;

/**
 * The VolumeUnit is a Unit that has a certain volume as limiting / determining factor.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class VolumeUnit extends Unit
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** volume of the unit in m3 */
    private double volume;

    /**
     * @param name name of the unit
     * @param volume determining volume of the unit, in m3
     */
    public VolumeUnit(final String name, final double volume)
    {
        super(name);
        this.volume = volume;
    }

    /**
     * @return Returns the volume in m3.
     */
    public double getVolume()
    {
        return this.volume;
    }
}
