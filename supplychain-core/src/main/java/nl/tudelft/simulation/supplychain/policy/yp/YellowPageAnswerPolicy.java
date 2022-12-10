package nl.tudelft.simulation.supplychain.policy.yp;

import java.util.List;
import java.util.Set;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Duration;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActorInterface;
import nl.tudelft.simulation.supplychain.message.store.trade.TradeMessageStoreInterface;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.RequestForQuote;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageRequest;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;
import nl.tudelft.simulation.supplychain.transport.TransportChoiceProvider;
import nl.tudelft.simulation.supplychain.transport.TransportOption;
import nl.tudelft.simulation.supplychain.transport.TransportOptionProvider;

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
public class YellowPageAnswerPolicy extends SupplyChainPolicy<YellowPageAnswer>
{
    /** the serial version uid. */
    private static final long serialVersionUID = 120221203;

    /** the provider of transport options betwween two locations. */
    private final TransportOptionProvider transportOptionProvider;

    /** the provider to choose between transport options. */
    private final TransportChoiceProvider transportChoiceProvider;

    /** the handling time of the handler in simulation time units. */
    private DistContinuousDuration handlingTime;

    /** the maximum time after which the RFQ will stop collecting quotes. */
    private final Duration cutoffDuration;

    /**
     * Constructs a new YellowPageAnswerHandler.
     * @param owner SupplyChainActorInterface; the owner of the policy
     * @param transportOptionProvider TransportOptionProvider; the provider of transport options betwween two locations
     * @param transportChoiceProvider TransportChoiceProvider; the provider to choose between transport options
     * @param handlingTime DistContinuousDuration; the distribution of the time to react on the YP answer
     * @param cutoffDuration Duration; the maximum time after which the RFQ will stop collecting quotes
     */
    public YellowPageAnswerPolicy(final SupplyChainActorInterface owner, final TransportOptionProvider transportOptionProvider,
            final TransportChoiceProvider transportChoiceProvider, final DistContinuousDuration handlingTime,
            final Duration cutoffDuration)
    {
        super("YellowPageAnswerPolicy", owner, YellowPageAnswer.class);
        Throw.whenNull(handlingTime, "handlingTime cannot be null");
        Throw.whenNull(transportOptionProvider, "transportOptionProvider cannot be null");
        Throw.whenNull(transportChoiceProvider, "transportChoiceProvider cannot be null");
        Throw.whenNull(cutoffDuration, "cutoffDuration cannot be null");
        this.transportOptionProvider = transportOptionProvider;
        this.transportChoiceProvider = transportChoiceProvider;
        this.handlingTime = handlingTime;
        this.cutoffDuration = cutoffDuration;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final YellowPageAnswer ypAnswer)
    {
        if (!isValidMessage(ypAnswer))
        {
            return false;
        }
        TradeMessageStoreInterface messageStore = getOwner().getMessageStore();
        YellowPageRequest ypRequest = ypAnswer.getYellowPageRequest();
        List<InternalDemand> internalDemandList =
                messageStore.getMessageList(ypRequest.getInternalDemandId(), InternalDemand.class);
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
            Set<TransportOption> transportOptions = this.transportOptionProvider.provideTransportOptions(supplier, getOwner());
            TransportOption transportOption =
                    this.transportChoiceProvider.chooseTransportOptions(transportOptions, ypRequest.getProduct().getSku());
            RequestForQuote rfq =
                    new RequestForQuote(getOwner(), supplier, internalDemand, transportOption, this.cutoffDuration);
            getOwner().sendMessage(rfq, delay);
        }
        return true;
    }

}
