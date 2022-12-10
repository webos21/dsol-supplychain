package nl.tudelft.simulation.supplychain.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Time;
import org.djutils.event.EventProducer;
import org.djutils.event.TimedEvent;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.role.inventory.InventoryActorInterface;
import nl.tudelft.simulation.supplychain.role.inventory.InventoryRole;

/**
 * Simple implementation of Inventory for a Trader. The information on stocked amounts is stored in a HashTable of
 * InventoryRecords. Events on stock changes are fired by Inventory, so subscribers who are interested in the stock amounts can
 * see what is going on in the Inventory.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Inventory extends EventProducer implements InventoryInterface, StockForecastInterface
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221210L;

    /** the actow that owns the inventory. */
    private final InventoryActorInterface owner;

    /** the InventoryRole of the owner. */
    private final InventoryRole inventoryRole;

    /** record keeping of the stock. */
    private Map<Product, InventoryRecord> inventoryRecords = new LinkedHashMap<Product, InventoryRecord>();

    /** Map of Product to Map of time to ArrayList of values for time moment: future changes. */
    private Map<Product, TreeMap<Time, ArrayList<Double>>> futureChanges = new LinkedHashMap<>();

    /**
     * Create a new Inventory for an actor.
     * @param owner the Trader that physically owns the stock.
     */
    public Inventory(final InventoryActorInterface owner)
    {
        Throw.whenNull(owner, "owner cannot be null");
        this.owner = owner;
        this.inventoryRole = owner.getInventoryRole();
    }

    /**
     * Create a new Inventory for an actor, with an initial amount of products.
     * @param owner the trader for which this is the stock
     * @param initialStock the initial stock
     */
    public Inventory(final InventoryActorInterface owner, final InventoryInterface initialStock)
    {
        this(owner);
        for (Product product : initialStock.getProducts())
        {
            addToInventory(product, initialStock.getActualAmount(product), product.getUnitMarketPrice());
            this.changeClaimedAmount(product, initialStock.getClaimedAmount(product));
            this.changeFutureClaimedAmount(product, initialStock.getClaimedAmount(product), owner.getSimulatorTime());
            this.changeOrderedAmount(product, initialStock.getOrderedAmount(product));
            this.changeFutureOrderedAmount(product, initialStock.getOrderedAmount(product), owner.getSimulatorTime());
            sendInventoryUpdateEvent(product);
        }
    }

    /** {@inheritDoc} */
    @Override
    public InventoryActorInterface getOwner()
    {
        return this.owner;
    }

    /** {@inheritDoc} */
    @Override
    public Set<Product> getProducts()
    {
        return this.inventoryRecords.keySet();
    }

    /** {@inheritDoc} */
    @Override
    public void addToInventory(final Product product, final double amount, final Money totalPrice)
    {
        InventoryRecord stockRecord = this.inventoryRecords.get(product);
        if (stockRecord == null)
        {
            stockRecord = new InventoryRecord(this.owner, this.owner.getSimulator(), product);
            this.inventoryRecords.put(product, stockRecord);
        }
        try
        {
            if (amount == 0.0)
            {
                throw new Exception("Amount is 0.0; leading to a divide by zero.");
            }
            stockRecord.addActualAmount(amount, totalPrice.divideBy(amount));
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            Logger.error(exception, "addStock");
        }
        this.sendStockUpdateEvent(stockRecord);
    }

    /** {@inheritDoc} */
    @Override
    public void addToInventory(final Shipment shipment)
    {
        InventoryRecord stockRecord = this.inventoryRecords.get(shipment.getProduct());
        if (stockRecord == null)
        {
            stockRecord = new InventoryRecord(this.owner, this.owner.getSimulator(), shipment.getProduct());
            this.inventoryRecords.put(shipment.getProduct(), stockRecord);
        }
        stockRecord.addActualAmount(shipment.getAmount(), shipment.getTotalCargoValue().divideBy(shipment.getAmount()));
        this.sendStockUpdateEvent(stockRecord);
    }

    /** {@inheritDoc} */
    @Override
    public double removeFromInventory(final Product product, final double amount)
    {
        InventoryRecord stockRecord = this.inventoryRecords.get(product);
        double actualAmount = 0.0;
        if (stockRecord != null)
        {
            actualAmount = Math.min(amount, stockRecord.getActualAmount());
        }
        // double unitprice = stockRecord.getUnitPrice();
        stockRecord.removeActualAmount(actualAmount);
        this.inventoryRole.checkInventory(product);
        this.sendStockUpdateEvent(stockRecord);
        return actualAmount;
    }

    /** {@inheritDoc} */
    @Override
    public double getActualAmount(final Product product)
    {
        InventoryRecord stockRecord = this.inventoryRecords.get(product);
        if (stockRecord == null)
        {
            return 0.0;
        }
        return stockRecord.getActualAmount();
    }

    /** {@inheritDoc} */
    @Override
    public double getClaimedAmount(final Product product)
    {
        InventoryRecord stockRecord = this.inventoryRecords.get(product);
        if (stockRecord == null)
        {
            return 0.0;
        }
        return stockRecord.getClaimedAmount();
    }

    /** {@inheritDoc} */
    @Override
    public double getOrderedAmount(final Product product)
    {
        InventoryRecord stockRecord = this.inventoryRecords.get(product);
        if (stockRecord == null)
        {
            return 0.0;
        }
        return stockRecord.getOrderedAmount();
    }

    /** {@inheritDoc} */
    @Override
    public boolean changeClaimedAmount(final Product product, final double delta)
    {
        InventoryRecord stockRecord = this.inventoryRecords.get(product);
        if (stockRecord == null)
        {
            return false;
        }
        stockRecord.changeClaimedAmount(delta);
        this.inventoryRole.checkInventory(product);
        this.sendStockUpdateEvent(stockRecord);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean changeFutureClaimedAmount(final Product product, final double delta, final Time time)
    {
        if (time.lt(this.owner.getSimulatorTime()))
        {
            Logger.error("changeFutureClaimedAmount - Time for the change is smaller than current simulator time (" + time + "<"
                    + this.owner.getSimulatorTime() + ").");
            return false;
        }

        if (delta < 0)
        {
            Logger.error("changeFutureOrderedAmount - The delta may not be smaller than 0 (" + delta + "<" + 0 + ").");
            return false;
        }

        if (!this.futureChanges.containsKey(product))
        {
            this.futureChanges.put(product, new TreeMap<Time, ArrayList<Double>>());
        }
        if (!this.futureChanges.get(product).containsKey(time))
        {
            this.futureChanges.get(product).put(time, new ArrayList<Double>());
        }
        // we consider a future claimed amount as a negative change for our stock value
        this.futureChanges.get(product).get(time).add(-delta);
        // this.sendForecastUpdateEvent(product);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean changeOrderedAmount(final Product product, final double delta)
    {
        InventoryRecord stockRecord = this.inventoryRecords.get(product);
        if (stockRecord == null)
        {
            return false;
        }
        stockRecord.changeOrderedAmount(delta);
        this.inventoryRole.checkInventory(product);
        this.sendStockUpdateEvent(stockRecord);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean changeFutureOrderedAmount(final Product product, final double delta, final Time time)
    {
        if (time.lt(this.owner.getSimulatorTime()))
        {
            Logger.error("changeFutureOrderedAmount - Time for the change is smaller than current simulator time (" + time + "<"
                    + this.owner.getSimulatorTime() + ").");
            return false;
        }
        if (delta < 0)
        {
            Logger.error("changeFutureOrderedAmount - The delta may not be smaller than 0 (" + delta + "<" + 0 + ").");
            return false;
        }

        if (!this.futureChanges.containsKey(product))
        {
            this.futureChanges.put(product, new TreeMap<Time, ArrayList<Double>>());
        }
        if (!this.futureChanges.get(product).containsKey(time))
        {
            this.futureChanges.get(product).put(time, new ArrayList<Double>());
        }
        this.futureChanges.get(product).get(time).add(delta);
        // this.sendForecastUpdateEvent(product);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public Money getUnitPrice(final Product product)
    {
        InventoryRecord stockRecord = this.inventoryRecords.get(product);
        if (stockRecord == null)
        {
            return product.getUnitMarketPrice();
        }
        return stockRecord.getUnitPrice();
    }

    /** {@inheritDoc} */
    @Override
    public int numberOfProducts()
    {
        return this.inventoryRecords.keySet().size();
    }

    /**
     * Method sendStockUpdateEvent.
     * @param stockRecord the stock record that is updated
     */
    public void sendStockUpdateEvent(final InventoryRecord stockRecord)
    {
        StockUpdateData data = new StockUpdateData(stockRecord.getProduct().getName(), stockRecord.getActualAmount(),
                stockRecord.getClaimedAmount(), stockRecord.getOrderedAmount());

        this.fireEvent(
                new TimedEvent<Time>(InventoryInterface.INVENTORY_CHANGE_EVENT, this, data, this.owner.getSimulatorTime()));
    }

    /**
     * Method sendStockUpdateEvent.
     * @param product Product; the product for which the stock is updated
     */
    @Override
    public void sendInventoryUpdateEvent(final Product product)
    {
        InventoryRecord stockRecord = this.inventoryRecords.get(product);
        if (stockRecord != null)
        {
            this.sendStockUpdateEvent(stockRecord);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return this.owner.toString() + "_stock";
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return this.owner.getName() + ".Stock";
    }

}
