package nl.tudelft.simulation.supplychain.inventory;

import java.io.Serializable;
import java.util.Set;

import org.djutils.event.EventProducerInterface;
import org.djutils.event.TimedEventType;

import nl.tudelft.simulation.supplychain.actor.StockKeepingActor;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The InventoryInterface describes the standard services that any object representing inventory in the supply chain project
 * should have. Methods are related to handling physical products in inventory itself, and three types of information on the
 * inventory: the available amount (really in the warehouse), the ordered amount (how many units did we order), and the claimed
 * amount (how many units are claimed for committed orders, as far as we know).
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface InventoryInterface extends Serializable, EventProducerInterface
{
    /** An event to indicate inventory levels changed. */
    TimedEventType INVENTORY_CHANGE_EVENT = new TimedEventType("INVENTORY_CHANGE_EVENT");

    /**
     * @return the trader who owns this inventory
     */
    StockKeepingActor getOwner();

    /**
     * Add products to the inventory.
     * @param product Product; the product
     * @param amount double; the amount
     * @param totalPrice the value of this amount of product
     */
    void addToInventory(Product product, double amount, Money totalPrice);

    /**
     * Add products to the inventory, based on a received Shipment.
     * @param shipment the shipment to add to the inventory
     */
    void addToInventory(Shipment shipment);

    /**
     * Remove products from the inventory.
     * @param product Product; the product
     * @param amount double; the amount
     * @return double the actual amount of the product taken out of inventory
     */
    double removeFromInventory(Product product, double amount);

    /**
     * Return an overview of the products that we have in inventory.
     * @return Set&lt;Product&gt;; an overview of the products that we have in inventory
     */
    Set<Product> getProducts();
    
    /**
     * Get the actual amount of a certain product in inventory.
     * @param product Product; the product
     * @return double the actual amount
     */
    double getActualAmount(Product product);

    /**
     * Get the claimed amount of a certain product in inventory.
     * @param product Product; the product
     * @return double the claimed amount
     */
    double getClaimedAmount(Product product);

    /**
     * Get the ordered amount of a certain product in inventory.
     * @param product Product; the product
     * @return double the ordered amount
     */
    double getOrderedAmount(Product product);

    /**
     * Update the claimed amount of a certain product in inventory.
     * @param product Product; the product
     * @param delta the delta (positive or negative)
     * @return boolean success or not
     */
    boolean changeClaimedAmount(Product product, double delta);

    /**
     * Update the ordered amount of a certain product in inventory.
     * @param product Product; the product
     * @param delta the delta (positive or negative)
     * @return boolean success or not
     */
    boolean changeOrderedAmount(Product product, double delta);

    /**
     * Return the unit price of a product (based on its SKU).
     * @param product Product; the product
     * @return double the price per unit
     */
    Money getUnitPrice(Product product);

    /**
     * Return the number of product types in inventory.
     * @return int number of products
     */
    int numberOfProducts();

    /**
     * Fires an update event on the current status of the inventory for the specific product.
     * @param product Product; the product to fire the update for
     */
    void sendInventoryUpdateEvent(Product product);
}
