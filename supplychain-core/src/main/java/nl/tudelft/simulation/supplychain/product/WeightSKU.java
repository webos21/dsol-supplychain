package nl.tudelft.simulation.supplychain.product;

/**
 * The WeightUnit is a Unit that has a certain weight as limiting / determining factor.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class WeightUnit extends Unit
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** weight of the unit in kg */
    private double weight;

    /**
     * @param name the name of the unit
     * @param weight the weight of the unit
     */
    public WeightUnit(final String name, final double weight)
    {
        super(name);
        this.weight = weight;
    }

    /**
     * @return Returns the weight in kg.
     */
    public double getWeight()
    {
        return this.weight;
    }
}
