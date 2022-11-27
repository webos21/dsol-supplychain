package nl.tudelft.simulation.supplychain.yellowpage;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.actor.Actor;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.capabilities.YPInterface;
import nl.tudelft.simulation.supplychain.actor.yellowpage.Topic;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.ContentStoreInterface;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * YellowPage.java.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SupplyChainYellowPage extends SupplyChainActor implements YPInterface
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the wrapped YP from the actor package. */
    private nl.tudelft.simulation.actor.yellowpage.YellowPage yp;

    /** the dictionary of product-actor combinations */
    private Map<Product, HashSet<SupplyChainActor>> dictionary = new LinkedHashMap<>();

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param contentStore
     */
    public SupplyChainYellowPage(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final Bank bank, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, contentStore);
        this.yp = new nl.tudelft.simulation.actor.yellowpage.YellowPage();
    }

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param initialBankBalance
     * @param contentStore
     */
    public SupplyChainYellowPage(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final Bank bank, final Money initialBankBalance, final ContentStoreInterface contentStore)
    {
        super(name, simulator, position, bank, initialBankBalance, contentStore);
        this.yp = new nl.tudelft.simulation.actor.yellowpage.YellowPage();
    }

    /**
     * Add a supplier to for a certain product
     * @param product the product with a set of suppliers.
     * @param supplier a supplier for that product.
     */
    public void addSupplier(final Product product, final SupplyChainActor supplier)
    {
        HashSet<SupplyChainActor> supplierSet = this.dictionary.get(product);
        if (supplierSet == null)
        {
            supplierSet = new LinkedHashSet<SupplyChainActor>();
            this.dictionary.put(product, supplierSet);
        }
        supplierSet.add(supplier);
    }

    /**
     * Remove a supplier for a certain product
     * @param product the product.
     * @param supplier the supplier for that product to be removed.
     */
    public void removeSupplier(final Product product, final SupplyChainActor supplier)
    {
        HashSet<SupplyChainActor> supplierSet = this.dictionary.get(product);
        if (supplierSet != null)
        {
            supplierSet.remove(supplier);
        }
    }

    /**
     * @param product the product for which to search for suppliers
     * @return the list of suppliers of the product (or an empty list)
     */
    public Set<SupplyChainActor> getSuppliers(final Product product)
    {
        Set<SupplyChainActor> supplierSet = new LinkedHashSet<>();
        if (this.dictionary.get(product) != null)
        {
            supplierSet.addAll(this.dictionary.get(product));
        }
        return supplierSet;
    }

    /** {@inheritDoc} */
    @Override
    public List<Actor> findActor(final String regex)
    {
        return this.yp.findActor(regex);
    }

    /** {@inheritDoc} */
    @Override
    public List<Actor> findActor(final String regex, final Topic category)
    {
        return this.yp.findActor(regex, category);
    }

    /** {@inheritDoc} */
    @Override
    public List<Actor> findActor(final Topic category)
    {
        return this.yp.findActor(category);
    }

    /** {@inheritDoc} */
    @Override
    public boolean register(final Actor actor, final Topic category)
    {
        return this.yp.register(actor, category);
    }

}
