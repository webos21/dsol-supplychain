package nl.tudelft.simulation.supplychain.ato;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.supplychain.messagehandlers.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.reference.FaxDevice;
import nl.tudelft.simulation.supplychain.actor.unit.dist.DistConstantDuration;
import nl.tudelft.simulation.supplychain.actor.yellowpage.Topic;
import nl.tudelft.simulation.supplychain.banking.Bank;
import nl.tudelft.simulation.supplychain.contentstore.memory.LeanContentStore;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicy;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicyMake;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicyStock;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicy;
import nl.tudelft.simulation.supplychain.policy.rfq.RequestForQuotePolicy;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.reference.Supplier;
import nl.tudelft.simulation.supplychain.reference.YellowPage;
import nl.tudelft.simulation.supplychain.roles.SellingRole;
import nl.tudelft.simulation.supplychain.stock.Stock;
import nl.tudelft.simulation.supplychain.stock.policies.RestockingPolicySafety;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * <p>
 * Copyright (c) 2016 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * </p>
 * $LastChangedDate: 2015-07-24 02:58:59 +0200 (Fri, 24 Jul 2015) $, @version $Revision: 1147 $, by $Author: averbraeck $,
 * initial version Oct 12, 2018 <br>
 * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href=
 *         "http://https://www.tudelft.nl/tbm/over-de-faculteit/afdelingen/multi-actor-systems/people/phd-candidates/b-bahareh-zohoori/">Bahareh
 *         Zohoori</a>
 */
public class ATOSupplier extends Supplier
{
    /** */
    private static final long serialVersionUID = 1L;

    /**
     * @param name
     * @param simulator
     * @param position
     * @param bank
     * @param initialBankAccount
     * @param product
     * @param initialStock
     * @param ypSupplier
     * @param stream
     * @param mts true if MTS, false if MTO
     */
    public ATOSupplier(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position, final Bank bank,
            final Money initialBankAccount, final Product product, final double initialStock, final YellowPage ypSupplier,
            final StreamInterface stream, final boolean mts)
    {
        super(name, simulator, position, bank, initialBankAccount, new LeanContentStore(simulator));

        // COMMUNICATION

        FaxDevice fax = new FaxDevice("fax-" + name, this.simulator);
        super.addSendingDevice(fax);
        MessageHandlerInterface faxChecker = new HandleAllMessages(this);
        super.addReceivingDevice(fax, faxChecker, new DistConstantDuration(new Duration(1.0, DurationUnit.HOUR)));

        // REGISTER IN YP

        ypSupplier.register(this, Topic.DEFAULT);
        ypSupplier.addSupplier(product, this);

        // STOCK

        Stock _stock = new Stock(this);
        _stock.addStock(product, initialStock, product.getUnitMarketPrice().multiplyBy(initialStock));
        super.setInitialStock(_stock);

        // SELLING HANDLERS

        RequestForQuotePolicy rfqHandler = new RequestForQuotePolicy(this, super.stock, 1.2,
                new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);

        OrderPolicy orderHandler;
        if (mts)
            orderHandler = new OrderPolicyStock(this, super.stock);
        else
            orderHandler = new OrderPolicyMake(this, super.stock);

        PaymentPolicy paymentHandler = new PaymentPolicy(this, super.bankAccount);

        SellingRole sellingRole = new SellingRole(this, this.simulator, rfqHandler, orderHandler, paymentHandler);
        super.setSellingRole(sellingRole);

        // RESTOCKING

        Iterator<Product> stockIter = super.stock.iterator();
        while (stockIter.hasNext())
        {
            // which list of products are considered here?
            Product stockProduct = stockIter.next();
            // the restocking policy will generate InternalDemand, handled by the BuyingRole
            new RestockingPolicySafety(super.stock, stockProduct, new Duration(24.0, DurationUnit.HOUR), false, initialStock,
                    true, 2.0 * initialStock, new Duration(14.0, DurationUnit.DAY));
        }

        // ANIMATION

        if (simulator instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable<>(this, simulator,
                        ATOSupplier.class.getResource("/nl/tudelft/simulation/supplychain/images/Supplier.gif"));
            }
            catch (RemoteException | NamingException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Bounds3d getBounds()
    {
        return new Bounds3d(25.0, 25.0, 1.0);
    }

}
