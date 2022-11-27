package nl.tudelft.simulation.supplychain.reference;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;

/**
 * Reference implementation for a DC.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistributionCenter extends Retailer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /**
     * @param name the name of the Distribution Center
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param contentStore the contentStore for the messages
     */
    public DistributionCenter(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final Bank bank, final ContentStoreInterface contentStore)
    {
        this(name, simulator, position, bank, new Money(0.0, MoneyUnit.USD), contentStore);
    }

    /**
     * @param name the name of the Distribution Center
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     * @param contentStore the contentStore for the messages
     */
    public DistributionCenter(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final Bank bank, final Money initialBankAccount, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, initialBankAccount, contentStore);
    }
}
