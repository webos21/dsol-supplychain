package nl.tudelft.simulation.supplychain.banking;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;

/**
 * The Bank to store the interest rates for the Bank accounts.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Bank extends Actor
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the interest rate for a positive bank account. */
    private double annualInterestRatePos = 0.025;

    /** the interest rate for a negative bank account. */
    private double annualInterestRateNeg = 0.08;

    /**
     * @param name the name
     * @param simulator the simulator
     * @param position the position
     */
    public Bank(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position)
    {
        super(name, simulator, position);
    }

    /**
     * @return Returns the annualInterestRateNeg.
     */
    public double getAnnualInterestRateNeg()
    {
        return this.annualInterestRateNeg;
    }

    /**
     * @param annualInterestRateNeg The annualInterestRateNeg to set.
     */
    public void setAnnualInterestRateNeg(final double annualInterestRateNeg)
    {
        this.annualInterestRateNeg = annualInterestRateNeg;
    }

    /**
     * @return Returns the annualInterestRatePos.
     */
    public double getAnnualInterestRatePos()
    {
        return this.annualInterestRatePos;
    }

    /**
     * @param annualInterestRatePos The annualInterestRatePos to set.
     */
    public void setAnnualInterestRatePos(final double annualInterestRatePos)
    {
        this.annualInterestRatePos = annualInterestRatePos;
    }
}
