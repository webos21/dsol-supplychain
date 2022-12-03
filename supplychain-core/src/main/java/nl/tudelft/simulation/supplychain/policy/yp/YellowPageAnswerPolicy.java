package nl.tudelft.simulation.supplychain.policy.yp;

import java.util.List;

import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.RequestForQuote;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessageTypes;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageRequest;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;

/**
 * The YellowPageAnswerHandler implements the business logic for a buyer who receives a YellowPageAnswer from a yellow page
 * supply chain actor. The most simple version that is implemented here, sends out RFQs to <b>all </b> the actors that are
 * reported back inside the YellowPageAnswer.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPageAnswerPolicy extends SupplyChainPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the handling time of the handler in simulation time units. */
    protected DistContinuousDuration handlingTime;

    /**
     * Constructs a new YellowPageAnswerHandler.
     * @param owner SupplyChainActor; the owner of the policy
     * @param handlingTime the distribution of the time to react on the YP answer
     */
    public YellowPageAnswerPolicy(final SupplyChainActor owner, final DistContinuousDuration handlingTime)
    {
        super("YellowPageAnswerPolicy", owner, TradeMessageTypes.YP_ANSWER);
        this.handlingTime = handlingTime;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final Message message)
    {
        if (!isValidMessage(message))
        {
            return false;
        }
        YellowPageAnswer ypAnswer = (YellowPageAnswer) message;
        TradeMessageStoreInterface contentStore = getOwner().getMessageStore();
        YellowPageRequest ypRequest = ypAnswer.getYellowPageRequest();
        List<InternalDemand> internalDemandList =
                contentStore.getMessageList(ypRequest.getInternalDemandId(), TradeMessageTypes.INTERNAL_DEMAND);
        if (internalDemandList.size() == 0) // we send it to ourselves, so it is 2x in the content store
        {
            Logger.warn("YPAnswerHandler - Actor '{}' could not find InternalDemandID '{}' for YPAnswer '{}'",
                    getOwner().getName(), ypRequest.getInternalDemandId(), ypAnswer.toString());
            return false;
        }
        InternalDemand internalDemand = internalDemandList.get(0);
        List<SupplyChainActor> potentialSuppliers = ypAnswer.getSuppliers();
        Duration delay = this.handlingTime.draw();
        for (SupplyChainActor supplier : potentialSuppliers)
        {
            RequestForQuote rfq = new RequestForQuote(getOwner(), supplier, internalDemand, internalDemand.getProduct(),
                    internalDemand.getAmount(), internalDemand.getEarliestDeliveryDate(),
                    internalDemand.getLatestDeliveryDate());
            getOwner().sendMessage(rfq, delay);
        }
        return true;
    }

}
