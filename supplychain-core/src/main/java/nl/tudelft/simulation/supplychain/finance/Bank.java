package nl.tudelft.simulation.supplychain.finance;

import org.djutils.draw.point.OrientedPoint2d;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.ActorAlreadyDefinedException;
import nl.tudelft.simulation.supplychain.dsol.SupplyChainModelInterface;

/**
 * The Bank to store the interest rates for the Bank accounts. In this case, we have chosen to not make the Bank work with
 * Messages, but this is of course possible to implement, e.g. to simulate risks of banks handling international transactions
 * slowly, or to simulate cyber attacks on the financial infrastructure.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Bank extends Actor
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221127L;

    /** the interest rate for a positive bank account. */
    private double annualInterestRatePos = 0.025;

    /** the interest rate for a negative bank account. */
    private double annualInterestRateNeg = 0.08;

    /**
     * Create a new Bank.
     * @param id String; the id of the bank
     * @param name String; the name of the bank
     * @param model SupplyChainModelInterface; the model
     * @param location OrientedPoint3d; the location on the map
     * @param locationDescription String; a description of the location (e.g., "Frankfurt")
     * @throws ActorAlreadyDefinedException when the bank has already been defined
     */
    public Bank(final String id, final String name, final SupplyChainModelInterface model, final OrientedPoint2d location,
            final String locationDescription) throws ActorAlreadyDefinedException
    {
        super(id, name, model, location, locationDescription);
    }

    /**
     * Return the negative annual interest rate.
     * @return double; negative annual interest rate
     */
    public double getAnnualInterestRateNeg()
    {
        return this.annualInterestRateNeg;
    }

    /**
     * Set a new negative annual interest rate.
     * @param annualInterestRateNeg double; new negative annual interest rate
     */
    public void setAnnualInterestRateNeg(final double annualInterestRateNeg)
    {
        this.annualInterestRateNeg = annualInterestRateNeg;
    }

    /**
     * Return the positive annual interest rate.
     * @return double; positive annual interest rate
     */
    public double getAnnualInterestRatePos()
    {
        return this.annualInterestRatePos;
    }

    /**
     * Set a new positive annual interest rate.
     * @param annualInterestRatePos double; new positive annual interest rate
     */
    public void setAnnualInterestRatePos(final double annualInterestRatePos)
    {
        this.annualInterestRatePos = annualInterestRatePos;
    }

}
