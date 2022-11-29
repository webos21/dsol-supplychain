package nl.tudelft.simulation.supplychain.product;

import org.djunits.value.vdouble.scalar.Mass;

/**
 * The WeightUnit is a Unit that has a certain weight as limiting / determining factor. Examples are mining materials and
 * agricultural products.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class WeightSKU extends SKU
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221129L;

    /** weight of the unit in kg. */
    private double weightKg;

    /**
     * Instantiate a Stock Keeping Unit based on weight in kg.
     * @param name String; the name of the unit
     * @param weightKg double; the weight of the unit in kg
     */
    public WeightSKU(final String name, final double weightKg)
    {
        super(name);
        this.weightKg = weightKg;
    }

    /**
     * Instantiate a Stock Keeping Unit based on weight provided as a Mass.
     * @param name String; the name of the unit
     * @param weight Mass; the weight of the unit
     */
    public WeightSKU(final String name, final Mass weight)
    {
        super(name);
        this.weightKg = weight.si;
    }

    /**
     * Return the weight in kg.
     * @return double; the weight in kg.
     */
    public double getWeightKg()
    {
        return this.weightKg;
    }

    /**
     * Return the weight as a Mass.
     * @return Mass; the weight of one SKU
     */
    public Mass getWeight()
    {
        return Mass.instantiateSI(this.weightKg);
    }

}
