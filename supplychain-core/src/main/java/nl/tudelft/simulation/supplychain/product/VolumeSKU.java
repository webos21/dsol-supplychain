package nl.tudelft.simulation.supplychain.product;

import org.djunits.value.vdouble.scalar.Volume;

/**
 * The VolumeUnit is a Unit that has a certain volume as limiting / determining factor. Examples are fluids and gases.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class VolumeSKU extends SKU
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221129L;

    /** volume of the unit in m3. */
    private double volumeM3;

    /**
     * @param name String; the name of the unit
     * @param volumeM3 double; determining volume of the unit, in m3
     */
    public VolumeSKU(final String name, final double volumeM3)
    {
        super(name);
        this.volumeM3 = volumeM3;
    }

    /**
     * @param name String; the name of the unit
     * @param volume Volume; determining volume of the unit
     */
    public VolumeSKU(final String name, final Volume volume)
    {
        super(name);
        this.volumeM3 = volume.si;
    }

    /**
     * Return the volume in m3.
     * @return double; the volume of the SKU in m3.
     */
    public double getVolumeM3()
    {
        return this.volumeM3;
    }
    
    /**
     * Return the volume as a Volume.
     * @return Volume; the volume of the SKU.
     */
    public Volume getVolume()
    {
        return Volume.instantiateSI(this.volumeM3);
    }
    
}
