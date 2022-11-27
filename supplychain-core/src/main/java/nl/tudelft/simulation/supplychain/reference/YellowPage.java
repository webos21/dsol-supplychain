package nl.tudelft.simulation.supplychain.reference;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.yellowpage.SupplyChainYellowPage;

/**
 * YellowPage.java.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPage extends SupplyChainYellowPage
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param contentStore
     */
    public YellowPage(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position, final Bank bank,
            final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, contentStore);
    }

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param initialBankBalance
     * @param contentStore
     */
    public YellowPage(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position, final Bank bank,
            final Money initialBankBalance, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, initialBankBalance, contentStore);
    }

}
