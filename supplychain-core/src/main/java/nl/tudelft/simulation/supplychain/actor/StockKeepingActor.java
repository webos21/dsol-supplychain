package nl.tudelft.simulation.supplychain.actor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.djutils.draw.point.OrientedPoint3d;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.stock.StockInterface;

/**
 * A Trader is a SupplyChainActor that maintains a Stock of products. The stock is not implemented with the Role, because
 * several roles might want to share the same stock, e.g. a product might be bought and then sold again from the same stock.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class StockKeepingActor extends SupplyChainActor
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the stock of the trader. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected StockInterface stock = null;

    /**
     * Build the StockKeepingActor with a Builder.
     * @param builder Builder; the Builder to use
     */
    public StockKeepingActor(final SupplyChainActor.Builder builder)
    {
        super(builder);
    }

    /**
     * Construct a new Actor.
     * @param name String; the name of the actor
     * @param messageHandler MessageHandlerInterface; the message handler to use
     * @param simulator SCSimulatorInterface; the simulator to use
     * @param location OrientedPoint3d; the location of the actor
     * @param locationDescription String; the location description of the actor (e.g., a city, country)
     * @param bank Bank; the bank for the BankAccount
     * @param initialBalance Money; the initial balance for the actor
     * @param messageStore TradeMessageStoreInterface; the message store for messages
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public StockKeepingActor(final String name, final MessageHandlerInterface messageHandler,
            final SCSimulatorInterface simulator, final OrientedPoint3d location, final String locationDescription,
            final Bank bank, final Money initialBalance, final TradeMessageStoreInterface messageStore)
    {
        super(name, messageHandler, simulator, location, locationDescription, bank, initialBalance, messageStore);
    }

    /**
     * Give the Trader some initial stock. Note: no clone, so make sure different stocks for different actors are truly
     * different. The method ONLY works when the stock object is still null
     * @param initialStock the initial stock to set
     */
    public void setInitialStock(final StockInterface initialStock)
    {
        if (this.stock == null)
        {
            this.stock = initialStock;
        }
        else
        {
            Logger.warn("setInitialStock - Stock initialized when not empty, initialization ignored");
        }
    }

    /**
     * Implement to check whether the stock is below some level, might trigger ordering of extra amount of the product.
     * @param product Product; the product to check the stock for.
     */
    public abstract void checkStock(Product product);

    /**
     * @return the raw materials
     */
    public List<Product> getProductsOnStock()
    {
        List<Product> products = new ArrayList<Product>();
        Iterator<Product> it = this.stock.iterator();
        while (it.hasNext())
        {
            Product product = it.next();
            products.add(product);
        }
        return products;
    }

    /**
     * Return the stock of this Actor.
     * @return StockInterface; the stock of this Actor
     */
    public StockInterface getStock()
    {
        return this.stock;
    }
}
