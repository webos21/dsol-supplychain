package nl.tudelft.simulation.supplychain.demo.reference;

import java.rmi.RemoteException;
import java.util.Iterator;

import javax.naming.NamingException;

import org.djunits.unit.DurationUnit;
import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.bounds.Bounds3d;
import org.djutils.draw.point.OrientedPoint3d;

import nl.tudelft.simulation.dsol.animation.D2.SingleImageRenderable;
import nl.tudelft.simulation.dsol.simulators.AnimatorInterface;
import nl.tudelft.simulation.jstats.distributions.DistConstant;
import nl.tudelft.simulation.jstats.distributions.DistTriangular;
import nl.tudelft.simulation.jstats.distributions.DistUniform;
import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.jstats.streams.StreamInterface;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.reference.FaxDevice;
import nl.tudelft.simulation.supplychain.actor.messaging.devices.reference.WebApplication;
import nl.tudelft.simulation.supplychain.actor.unit.dist.DistConstantDuration;
import nl.tudelft.simulation.supplychain.actor.yellowpage.Topic;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.finance.Bank;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.inventory.Stock;
import nl.tudelft.simulation.supplychain.inventory.policies.RestockingPolicySafety;
import nl.tudelft.simulation.supplychain.message.handler.MessageHandlerInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.LeanTradeMessageStore;
import nl.tudelft.simulation.supplychain.messagehandlers.HandleAllMessages;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyYP;
import nl.tudelft.simulation.supplychain.policy.order.AbstractOrderPolicy;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicyMake;
import nl.tudelft.simulation.supplychain.policy.order.OrderPolicyStock;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicy;
import nl.tudelft.simulation.supplychain.policy.payment.PaymentPolicyEnum;
import nl.tudelft.simulation.supplychain.policy.quote.QuoteComparatorEnum;
import nl.tudelft.simulation.supplychain.policy.quote.AbstractQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.quote.QuotePolicyAll;
import nl.tudelft.simulation.supplychain.policy.rfq.RequestForQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.AbstractShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.ShipmentPolicyConsume;
import nl.tudelft.simulation.supplychain.policy.yp.YellowPageAnswerPolicy;
import nl.tudelft.simulation.supplychain.product.Product;
import nl.tudelft.simulation.supplychain.production.DelayProductionService;
import nl.tudelft.simulation.supplychain.production.ProductionService;
import nl.tudelft.simulation.supplychain.reference.Manufacturer;
import nl.tudelft.simulation.supplychain.reference.YellowPage;
import nl.tudelft.simulation.supplychain.role.buying.BuyingRoleYP;
import nl.tudelft.simulation.supplychain.role.selling.SellingRole;
import nl.tudelft.simulation.supplychain.transport.TransportMode;

/**
 * MtsMtomarket.java. <br>
 * <br>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DemoManufacturer extends Manufacturer
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
     * @param ypCustomer
     * @param ypProduction
     * @param stream
     * @param mts true if MTS, false if MTO
     */
    public DemoManufacturer(final String name, final SCSimulatorInterface simulator, final OrientedPoint3d position,
            final Bank bank, final Money initialBankAccount, final Product product, final double initialStock,
            final YellowPage ypCustomer, final YellowPage ypProduction, final StreamInterface stream, final boolean mts)
    {
        super(name, simulator, position, bank, initialBankAccount, new LeanTradeMessageStore(simulator));

        // COMMUNICATION

        WebApplication www = new WebApplication("Web-" + name, this.simulator);
        super.addSendingDevice(www);
        MessageHandlerInterface webSystem = new HandleAllMessages(this);
        super.addReceivingDevice(www, webSystem, new DistConstantDuration(new Duration(10.0, DurationUnit.SECOND)));

        FaxDevice fax = new FaxDevice("fax-" + name, this.simulator);
        super.addSendingDevice(fax);
        MessageHandlerInterface faxChecker = new HandleAllMessages(this);
        super.addReceivingDevice(fax, faxChecker, new DistConstantDuration(new Duration(1.0, DurationUnit.HOUR)));

        // REGISTER IN YP

        ypProduction.register(this, Topic.DEFAULT);
        ypProduction.addSupplier(product, this);

        // STOCK, ALSO FOR BOM ENTRIES

        Stock _stock = new Stock(this);
        _stock.addStock(product, initialStock, product.getUnitMarketPrice().multiplyBy(initialStock));
        for (Product p : product.getBillOfMaterials().getMaterials().keySet())
        {
            double amount = initialStock * product.getBillOfMaterials().getMaterials().get(p);
            _stock.addStock(p, amount, p.getUnitMarketPrice().multiplyBy(amount));
        }
        super.setInitialStock(_stock);

        // BUYING HANDLERS

        DistContinuousDuration administrativeDelayInternalDemand =
                new DistContinuousDuration(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        InternalDemandPolicyYP internalDemandHandler = new InternalDemandPolicyYP(this, administrativeDelayInternalDemand,
                ypProduction, new Length(1E6, LengthUnit.METER), 1000, super.stock);

        DistContinuousDuration administrativeDelayYellowPageAnswer =
                new DistContinuousDuration(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        YellowPageAnswerPolicy ypAnswerHandler = new YellowPageAnswerPolicy(this, administrativeDelayYellowPageAnswer);

        DistContinuousDuration administrativeDelayQuote =
                new DistContinuousDuration(new DistTriangular(stream, 2, 2.5, 3), DurationUnit.HOUR);
        AbstractQuotePolicy quoteHandler =
                new QuotePolicyAll(this, QuoteComparatorEnum.SORT_PRICE_DATE_DISTANCE, administrativeDelayQuote, 0.4, 0);

        OrderConfirmationPolicy orderConfirmationHandler = new OrderConfirmationPolicy(this);

        AbstractShipmentPolicy shipmentHandler = new ShipmentPolicyConsume(this);

        DistContinuousDuration paymentDelay = new DistContinuousDuration(new DistConstant(stream, 0.0), DurationUnit.HOUR);
        BillPolicy billHandler = new BillPolicy(this, this.getBankAccount(), PaymentPolicyEnum.PAYMENT_ON_TIME, paymentDelay);

        BuyingRoleYP buyingRole = new BuyingRoleYP(this, simulator, internalDemandHandler, ypAnswerHandler, quoteHandler,
                orderConfirmationHandler, shipmentHandler, billHandler);
        this.setBuyingRole(buyingRole);

        // SELLING HANDLERS

        RequestForQuotePolicy rfqHandler = new RequestForQuotePolicy(this, super.stock, 1.2,
                new DistConstantDuration(new Duration(1.23, DurationUnit.HOUR)), TransportMode.PLANE);

        AbstractOrderPolicy orderHandler;
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
            Product stockProduct = stockIter.next();
            // the restocking policy will generate InternalDemand, handled by the BuyingRole
            new RestockingPolicySafety(super.stock, stockProduct, new Duration(24.0, DurationUnit.HOUR), false, initialStock,
                    true, 2.0 * initialStock, new Duration(14.0, DurationUnit.DAY));
        }

        // MANUFACTURING

        ProductionService productionService = new DelayProductionService(this, super.getStock(), product,
                new DistContinuousDuration(new DistUniform(stream, 5.0, 10.0), DurationUnit.DAY), true, true, 0.2);
        getProduction().addProductionService(productionService);

        // ANIMATION

        if (simulator instanceof AnimatorInterface)
        {
            try
            {
                new SingleImageRenderable<>(this, simulator,
                        DemoManufacturer.class.getResource("/nl/tudelft/simulation/supplychain/images/Manufacturer.gif"));
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
