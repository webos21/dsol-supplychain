package nl.tudelft.simulation.supplychain.reference;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;

/**
 * Reference implementation for a DC.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DistributionCenter extends Retailer
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221206L;

    /**
     * @param name String; the name of the DistributionCenter
     * @param messageHandler MessageHandlerInterface; the message handler to use
     * @param simulator SCSimulatorInterface; the simulator
     * @param location Location; the locatrion of the actor on the map or grid
     * @param locationDescription String; a description of the location of the DistributionCenter
     * @param bank Bank; the bank of the DistributionCenter
     * @param initialBalance Money; the initial bank balance
     * @param messageStore TradeMessageStoreInterface; the messageStore for the messages
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public DistributionCenter(final String name, final MessageHandlerInterface messageHandler,
            final SCSimulatorInterface simulator, final OrientedPoint3d location, final String locationDescription,
            final Bank bank, final Money initialBalance, final TradeMessageStoreInterface messageStore)
    {
        super(name, messageHandler, simulator, location, locationDescription, bank, initialBalance, messageStore);
    }

}
