package nl.tudelft.simulation.supplychain.role.buying;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.trade.Bill;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.OrderConfirmation;
import nl.tudelft.simulation.supplychain.message.trade.Quote;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.AbstractInternalDemandPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyOrder;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyRFQ;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyYP;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.quote.AbstractQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.AbstractShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.yp.YellowPageAnswerPolicy;

/**
 * The buying role is a role that can handle several types of message content: internal demand, order confirmation, bill, and
 * shipment. Depending on the extension of the BuyingRole, which actually indicates the type if InternalDemandPolicy used,
 * several other messages can be handled as well. For the InternalDemandPolicyOrder, no extra types are necessary. For the
 * InternalDemandhandlerRFQ, a Quote has to be handled as well. For an InternalDemandhandlerYP, a YellowPageAnswer can be
 * received, and has to be handled.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BuyingRoleYP extends Role implements BuyingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Constructs a new BuyingRole for Generic Demand - Confirmation - Shipment - Bill.
     * @param owner the owner this role
     * @param simulator the simulator to schedule on
     * @param internalDemandPolicy the internal demand handler
     * @param orderConfirmationPolicy the order confirmation handler
     * @param shipmentPolicy the shipment handler
     * @param billPolicy the bill handler
     */
    public BuyingRoleYP(final SupplyChainActor owner, final SCSimulatorInterface simulator,
            final AbstractInternalDemandPolicy internalDemandPolicy, final OrderConfirmationPolicy orderConfirmationPolicy,
            final AbstractShipmentPolicy shipmentPolicy, final BillPolicy billPolicy)
    {
        super(owner, owner.getName() + "-BUYING", simulator);
        addMessagePolicy(InternalDemand.class, internalDemandPolicy);
        addMessagePolicy(OrderConfirmation.class, orderConfirmationPolicy);
        addMessagePolicy(Shipment.class, shipmentPolicy);
        addMessagePolicy(Bill.class, billPolicy);
    }

    /**
     * Constructs a new BuyingRole for Order-based Demand - Confirmation - Shipment - Bill.
     * @param owner the owner this role
     * @param simulator the simulator to schedule on
     * @param internalDemandPolicy the internal demand handler, based on an order
     * @param orderConfirmationPolicy the order confirmation handler
     * @param shipmentPolicy the shipment handler
     * @param billPolicy the bill handler
     */
    public BuyingRoleYP(final SupplyChainActor owner, final SCSimulatorInterface simulator,
            final InternalDemandPolicyOrder internalDemandPolicy, final OrderConfirmationPolicy orderConfirmationPolicy,
            final AbstractShipmentPolicy shipmentPolicy, final BillPolicy billPolicy)
    {
        super(owner, owner.getName() + "-BUYING", simulator);
        addMessagePolicy(InternalDemand.class, internalDemandPolicy);
        addMessagePolicy(OrderConfirmation.class, orderConfirmationPolicy);
        addMessagePolicy(Shipment.class, shipmentPolicy);
        addMessagePolicy(Bill.class, billPolicy);
    }

    /**
     * Constructs a new BuyingRole for Demand - Quote - Confirmation - Shipment - Bill.
     * @param owner the owner this role
     * @param simulator the simulator to schedule on
     * @param internalDemandPolicy the internal demand handler, results in ending out an RFQ
     * @param quotePolicy the quote handler
     * @param orderConfirmationPolicy the order confirmation handler
     * @param shipmentPolicy the shipment handler
     * @param billPolicy the bill handler
     */
    public BuyingRoleYP(final SupplyChainActor owner, final SCSimulatorInterface simulator,
            final InternalDemandPolicyRFQ internalDemandPolicy, final AbstractQuotePolicy quotePolicy,
            final OrderConfirmationPolicy orderConfirmationPolicy, final AbstractShipmentPolicy shipmentPolicy,
            final BillPolicy billPolicy)
    {
        super(owner, owner.getName() + "-BUYING", simulator);
        addMessagePolicy(InternalDemand.class, internalDemandPolicy);
        addMessagePolicy(Quote.class, quotePolicy);
        addMessagePolicy(OrderConfirmation.class, orderConfirmationPolicy);
        addMessagePolicy(Shipment.class, shipmentPolicy);
        addMessagePolicy(Bill.class, billPolicy);
    }

    /**
     * Construct a new BuyingRole for Demand - YPAnswer - Quote - Confirmation - Shipment - Bill.
     * @param owner the owner this role
     * @param internalDemandPolicy the internal demand handler, results in ending out an RFQ
     * @param ypAnswerPolicy the yellow page answer handler
     * @param quotePolicy the quote handler
     * @param orderConfirmationPolicy the order confirmation handler
     * @param shipmentPolicy the shipment handler
     * @param billPolicy the bill handler
     */
    public BuyingRoleYP(final SupplyChainActor owner, 
            final InternalDemandPolicyYP internalDemandPolicy, final YellowPageAnswerPolicy ypAnswerPolicy,
            final AbstractQuotePolicy quotePolicy, final OrderConfirmationPolicy orderConfirmationPolicy,
            final AbstractShipmentPolicy shipmentPolicy, final BillPolicy billPolicy)
    {
        super(owner);
        addMessagePolicy(internalDemandPolicy);
        addMessagePolicy(ypAnswerPolicy);
        addMessagePolicy(quotePolicy);
        addMessagePolicy(orderConfirmationPolicy);
        addMessagePolicy(shipmentPolicy);
        addMessagePolicy(billPolicy);
    }

}
