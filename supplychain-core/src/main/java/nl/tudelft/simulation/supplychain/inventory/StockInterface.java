package nl.tudelft.simulation.supplychain.inventory;

import java.io.Serializable;
import java.util.Iterator;

import org.djutils.event.EventProducerInterface;
import org.djutils.event.TimedEventType;

import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The StockInterface describes the standard services that any object representing Stock in the supply chain project should
 * have. Methods are related to handling physical stock itself, and three types of information on the stock: the available
 * amount (really in the warehouse), the ordered amount (how many units did we order), and the claimed amount (how many units
 * are claimed for committed orders, as far as we know).
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface StockInterface extends Serializable, EventProducerInterface
{
    /** An event to indicate stock levels changed. */
    TimedEventType STOCK_CHANGE_EVENT = new TimedEventType("STOCK_CHANGE_EVENT");

    /**
     * @return the trader who owns this stock
     */
    StockKeepingActor getOwner();

    /**
     * Method addStock.
     * @param product Product; the product
     * @param amount double; the amount
     * @param totalPrice the value of this amount of product
     */
    void addStock(Product product, double amount, Money totalPrice);

    /**
     * Method addStock.
     * @param shipment the shipment to add to the stock
     */
    void addStock(Shipment shipment);

    /**
     * Method removeStock.
     * @param product Product; the product
     * @param amount double; the amount
     * @return double the actual amount of the product taken out of stock
     */
    double removeStock(Product product, double amount);

    /**
     * Method getActualAmount.
     * @param product Product; the product
     * @return double the actual amount
     */
    double getActualAmount(Product product);

    /**
     * Method getClaimedAmount.
     * @param product Product; the product
     * @return double the claimed amount
     */
    double getClaimedAmount(Product product);

    /**
     * Method getOrderedAmount.
     * @param product Product; the product
     * @return double the ordered amount
     */
    double getOrderedAmount(Product product);

    /**
     * Method changeClaimedAmount.
     * @param product Product; the product
     * @param delta the delta (positive or negative)
     * @return boolean success or not
     */
    boolean changeClaimedAmount(Product product, double delta);

    /**
     * Method changeOrderedAmount.
     * @param product Product; the product
     * @param delta the delta (positive or negative)
     * @return boolean success or not
     */
    boolean changeOrderedAmount(Product product, double delta);

    /**
     * Method getUnitPrice.
     * @param product Product; the product
     * @return double the price per unit
     */
    Money getUnitPrice(Product product);

    /**
     * Method iterator.
     * @return the iterator
     */
    Iterator<Product> iterator();

    /**
     * give the number of product types in stock.
     * @return int number of products
     */
    int numberOfProducts();

    /**
     * fires an update event on the current status of the stock for the specific product.
     * @param product Product; the product to fire the update for
     */
    void sendStockUpdateEvent(Product product);
}
