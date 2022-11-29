package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;

/**
 * SKU stands for Stock Keeping Unit. At this moment, it just is a placeholder for a unit name, providing some standard static
 * Unit types. We could provide extensions of this class allowing to calculate weight or volume of products, based on the
 * density of the product. It could also provide some basic measures to translate the number of units from one Unit class to the
 * other.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SKU implements Serializable
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221129L;

    /** units without weight or volume : PIECE */
    public static final SKU PIECE = new SKU("piece");

    /** units without weight or volume : PALLET */
    public static final SKU PALLET = new SKU("pallet");

    /** units without weight or volume : BOX */
    public static final SKU BOX = new SKU("box");

    /** units with volume : 20 FT CONTAINER */
    public static final SKU CONTAINER20FT = new VolumeSKU("container 20ft", 33.2);

    /** units with volume : 40 FT CONTAINER */
    public static final SKU CONTAINER40FT = new VolumeSKU("container 40ft", 67.6);

    /** units with volume : M3 or cubic meter */
    public static final SKU M3 = new VolumeSKU("m3", 1.0);

    /** units with weight : KG */
    public static final SKU KG = new WeightSKU("kg", 1.0);

    /** the name of the unit for printing purposes */
    protected String name;

    /**
     * Constructor for Unit.
     * @param name the name of the unit for printing purposes.
     */
    public SKU(final String name)
    {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.name;
    }

    /**
     * @return the name.
     */
    public String getName()
    {
        return this.name;
    }
}
