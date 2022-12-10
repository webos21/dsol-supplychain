package nl.tudelft.simulation.supplychain.reference;

import java.util.ArrayList;
import java.util.List;

import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.finance.MoneyUnit;
import nl.tudelft.simulation.supplychain.message.store.MessageStoreInterface;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.production.Production;
import nl.tudelft.simulation.supplychain.production.ProductionService;
import nl.tudelft.simulation.supplychain.role.producing.ProducingActorInterface;

/**
 * Reference implementation for a manufacturer.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class Manufacturer extends DistributionCenter implements ProducingActorInterface
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the production capabilities of this manufacturer. */
    private Production production;

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param messageStore the messageStore for the messages
     */
    public Manufacturer(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final Bank bank, final MessageStoreInterface messageStore)
    {
        this(name, simulator, position, bank, new Money(0.0, MoneyUnit.USD), messageStore);
    }

    /**
     * @param name the name of the manufacturer
     * @param simulator the simulator to use
     * @param position the position on the map
     * @param bank the bank
     * @param initialBankAccount the initial bank balance
     * @param messageStore the messageStore for the messages
     */
    public Manufacturer(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final Bank bank, final Money initialBankAccount, final MessageStoreInterface messageStore)
    {
        super(name, simulator, position, bank, initialBankAccount, messageStore);
        this.production = new Production(this);
    }

    /** {@inheritDoc} */
    @Override
    public Production getProduction()
    {
        return this.production;
    }

    /**
     * Add a production service for the manufacturer.
     * @param productionService the service to add
     */
    public void addProductionService(final ProductionService productionService)
    {
        this.production.addProductionService(productionService);
    }

    /**
     * @return the raw materials
     */
    public List<Product> getRawMaterials()
    {
        List<Product> rawMaterials = new ArrayList<Product>();
        for (Product product : super.stock.getProducts())
        {
            if (product.getBillOfMaterials().getMaterials().size() == 0)
            {
                rawMaterials.add(product);
            }
        }
        return rawMaterials;
    }

    /**
     * @return the end products
     */
    public List<Product> getEndProducts()
    {
        List<Product> endProducts = new ArrayList<Product>();
        for (Product product : super.stock.getProducts())
        {
            if (product.getBillOfMaterials().getMaterials().size() > 0)
            {
                endProducts.add(product);
            }
        }
        return endProducts;
    }
}
