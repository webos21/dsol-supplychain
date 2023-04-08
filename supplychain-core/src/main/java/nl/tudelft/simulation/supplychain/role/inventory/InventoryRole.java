package nl.tudelft.simulation.supplychain.role.inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djunits.Throw;

import nl.tudelft.simulation.supplychain.actor.SupplyChainRole;
import nl.tudelft.simulation.supplychain.inventory.Inventory;
import nl.tudelft.simulation.supplychain.inventory.InventoryInterface;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * The inventory role is a role that handles the storage of products, which can be raw materials for production or finished
 * goods. The InventoyRole can trigger production and purchasing to replenish the inventory.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class InventoryRole extends SupplyChainRole
{
    /** */
    private static final long serialVersionUID = 20221206L;

    /** the inventory with products. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected final InventoryInterface inventory;

    /** the restocking services per product. */
    private final Map<Product, RestockingServiceInterface> restockingServices = new LinkedHashMap<>();

    /**
     * Create an InventoryRole object for an actor, with an empty inventory.
     * @param owner SupplyChainActor; the owner of this role
     */
    public InventoryRole(final InventoryActor owner)
    {
        super(owner);
        this.inventory = new Inventory(owner);
    }

    /**
     * Create an InventoryRole object for an actor.
     * @param owner SupplyChainActor; the owner of this role
     * @param initialInventory InventoryInterface; the Inventory to use within this role
     */
    public InventoryRole(final InventoryActor owner, final InventoryInterface initialInventory)
    {
        super(owner);
        Throw.whenNull(initialInventory, "initialInventory cannot be null");
        this.inventory = new Inventory(owner, initialInventory);
    }

    /**
     * Add a restocking service to this role.
     * @param restockingService RestockingServiceInterface; the restocking service to add to this role
     */
    public void addRestockingService(final RestockingServiceInterface restockingService)
    {
        Throw.whenNull(restockingService, "restockingService cannot be null");
        Throw.when(!restockingService.getInventory().equals(this.inventory), IllegalArgumentException.class,
                "Inventory of the restocking service does not belong to Actor of InventoryRole");
        this.restockingServices.put(restockingService.getProduct(), restockingService);
    }

    /**
     * Implement to check whether the inventory is below some level, might trigger ordering of extra amount of the product.
     * @param product Product; the product to check the inventory for.
     */
    public abstract void checkInventory(Product product);

    /**
     * @return the raw materials
     */
    public List<Product> getProductsInInventory()
    {
        List<Product> products = new ArrayList<Product>();
        for (Product product : this.inventory.getProducts())
        {
            products.add(product);
        }
        return products;
    }

    /**
     * Return the inventory of this Role.
     * @return InventoryInterface; the inventory of this Role
     */
    public InventoryInterface getInventory()
    {
        return this.inventory;
    }
}
