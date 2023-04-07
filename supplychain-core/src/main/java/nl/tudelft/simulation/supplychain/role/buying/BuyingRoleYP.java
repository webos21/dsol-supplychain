package nl.tudelft.simulation.supplychain.role.buying;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.policy.bill.BillPolicy;
import nl.tudelft.simulation.supplychain.policy.internaldemand.InternalDemandPolicyYP;
import nl.tudelft.simulation.supplychain.policy.orderconfirmation.OrderConfirmationPolicy;
import nl.tudelft.simulation.supplychain.policy.quote.AbstractQuotePolicy;
import nl.tudelft.simulation.supplychain.policy.shipment.AbstractShipmentPolicy;
import nl.tudelft.simulation.supplychain.policy.yp.YellowPageAnswerPolicy;

/**
 * The buying role with yellow pages is a role that organizes the buying based on a YellowPageRequest, and continues from there.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class BuyingRoleYP extends BuyingRole
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221205L;

    /**
     * Construct a new BuyingRole for Demand - YPAnswer - Quote - Confirmation - Shipment - Bill.
     * @param owner SupplyChainActor; the actor to which this role belongs
     * @param internalDemandPolicy the internal demand handler, results in sending out an RFQ
     * @param ypAnswerPolicy the yellow page answer handler
     * @param quotePolicy the quote handler
     * @param orderConfirmationPolicy the order confirmation handler
     * @param shipmentPolicy the shipment handler
     * @param billPolicy the bill handler
     */
    public BuyingRoleYP(final SupplyChainActor owner, final InternalDemandPolicyYP internalDemandPolicy,
            final YellowPageAnswerPolicy ypAnswerPolicy, final AbstractQuotePolicy quotePolicy,
            final OrderConfirmationPolicy orderConfirmationPolicy, final AbstractShipmentPolicy shipmentPolicy,
            final BillPolicy billPolicy)
    {
        super(owner);
        addMessagePolicy(internalDemandPolicy);
        addMessagePolicy(ypAnswerPolicy);
        addMessagePolicy(quotePolicy);
        addMessagePolicy(orderConfirmationPolicy);
        addMessagePolicy(shipmentPolicy);
        addMessagePolicy(billPolicy);
    }

    /** {@inheritDoc} */
    @Override
    public String getId()
    {
        return getOwner().getName() + "-BUYING(yp)";
    }

}
