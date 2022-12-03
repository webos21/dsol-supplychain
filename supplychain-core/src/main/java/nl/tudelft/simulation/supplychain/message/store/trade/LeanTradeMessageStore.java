package nl.tudelft.simulation.supplychain.message.store.trade;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.djunits.Throw;
import org.djunits.unit.DurationUnit;
import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.dsol.SCSimulatorInterface;
import nl.tudelft.simulation.supplychain.message.MessageType;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.message.trade.OrderConfirmation;
import nl.tudelft.simulation.supplychain.message.trade.OrderStandalone;
import nl.tudelft.simulation.supplychain.message.trade.Quote;
import nl.tudelft.simulation.supplychain.message.trade.RequestForQuote;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessageTypes;

/**
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class LeanTradeMessageStore extends TradeMessageStore
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the simulator to schedule time-out events. */
    protected SCSimulatorInterface simulator;

    /** the map of unanswered content. */
    private Map<Serializable, TradeMessage> unansweredContentMap =
            Collections.synchronizedMap(new LinkedHashMap<Serializable, TradeMessage>());

    /**
     * @param simulator the simulator
     */
    public LeanTradeMessageStore(final SCSimulatorInterface simulator)
    {
        Throw.whenNull(simulator, "simulator cannot be null");
        this.simulator = simulator;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void addMessage(final TradeMessage message, final boolean sent)
    {
        super.addMessage(message, sent);
        MessageType type = message.getType();
        try
        {
            // schedule the removal after the 'lifetime' of the content is unanswered
            if (type.equals(TradeMessageTypes.INTERNAL_DEMAND))
            {
                InternalDemand internalDemand = (InternalDemand) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                Time date = Time.max(this.simulator.getAbsSimulatorTime(), internalDemand.getLatestDeliveryDate());
                this.simulator.scheduleEventAbs(date, this, this, "internalDemandTimeout",
                        new Serializable[] {internalDemand, Boolean.valueOf(sent)});
            }
            else if (type.equals(TradeMessageTypes.RFQ) && !sent)
            {
                RequestForQuote rfq = (RequestForQuote) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                this.unansweredContentMap.remove(rfq.getInternalDemandId());
                Time date = Time.max(this.simulator.getAbsSimulatorTime(), rfq.getCutoffDate());
                this.simulator.scheduleEventAbs(date, this, this, "requestForQuoteTimeout",
                        new Serializable[] {rfq, Boolean.valueOf(sent)});
            }
            else if (type.equals(TradeMessageTypes.RFQ) && sent)
            {
                RequestForQuote rfq = (RequestForQuote) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                this.unansweredContentMap.remove(rfq.getInternalDemandId());
                Time date = Time.max(this.simulator.getAbsSimulatorTime(),
                        rfq.getCutoffDate().plus(new Duration(1.0, DurationUnit.DAY)));
                this.simulator.scheduleEventAbs(date, this, this, "requestForQuoteTimeout",
                        new Serializable[] {rfq, Boolean.valueOf(sent)});
            }
            else if (type.equals(TradeMessageTypes.QUOTE))
            {
                Quote quote = (Quote) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                this.unansweredContentMap.remove(quote.getRequestForQuote().getUniqueId());
                Time date = Time.max(quote.getProposedDeliveryDate(),
                        quote.getRequestForQuote().getCutoffDate().plus(new Duration(1.0, DurationUnit.DAY)));
                date = Time.max(date, quote.getRequestForQuote().getLatestDeliveryDate());
                date = Time.max(this.simulator.getAbsSimulatorTime(), date);
                this.simulator.scheduleEventAbs(date, this, this, "quoteTimeout",
                        new Serializable[] {quote, Boolean.valueOf(sent)});
            }
            else if (type.equals(TradeMessageTypes.ORDER_BASED_ON_QUOTE))
            {
                OrderBasedOnQuote order = (OrderBasedOnQuote) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                this.unansweredContentMap.remove(order.getQuote().getUniqueId());
                Time date = Time.max(order.getDeliveryDate(), order.getQuote().getProposedDeliveryDate());
                date = Time.max(date, order.getQuote().getRequestForQuote().getLatestDeliveryDate());
                date = Time.max(this.simulator.getAbsSimulatorTime(), date);
                this.simulator.scheduleEventAbs(date, this, this, "orderBasedOnQuoteTimeout",
                        new Serializable[] {order, Boolean.valueOf(sent)});
            }
            else if (type.equals(TradeMessageTypes.ORDER_STANDALONE))
            {
                OrderStandalone order = (OrderStandalone) message;
                this.unansweredContentMap.put(message.getUniqueId(), message);
                this.unansweredContentMap.remove(order.getInternalDemandId());
                Time date = Time.max(this.simulator.getAbsSimulatorTime(), order.getDeliveryDate());
                this.simulator.scheduleEventAbs(date, this, this, "orderStandAloneTimeout",
                        new Serializable[] {order, Boolean.valueOf(sent)});
            }
            else if (type.equals(TradeMessageTypes.ORDER_CONFIRMATION))
            {
                OrderConfirmation orderConfirmation = (OrderConfirmation) message;
                this.unansweredContentMap.remove(orderConfirmation.getOrder().getUniqueId());
            }
            else if (type.equals(TradeMessageTypes.PRODUCTION_ORDER) || type.equals(TradeMessageTypes.SHIPMENT)
                    || type.equals(TradeMessageTypes.BILL) || type.equals(TradeMessageTypes.PAYMENT)
                    || type.equals(TradeMessageTypes.YP_REQUEST)
                    || type.equals(TradeMessageTypes.YP_ANSWER))
            {
                // nothing to do
            }
            else
            {
                Logger.warn("addContent - could not find content class {}", type);
            }
        }
        catch (Exception e)
        {
            Logger.warn(e, "addContent");
        }
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeMessage(final TradeMessage message, final boolean sent)
    {
        super.removeMessage(message, sent);
        this.unansweredContentMap.remove(message.getUniqueId());
    }

    /**
     * @param internalDemand the internal demand
     * @param sent boolean sent
     */
    protected void internalDemandTimeout(final InternalDemand internalDemand, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(internalDemand.getUniqueId()))
        {
            this.removeMessage(internalDemand, sent);
        }
    }

    /**
     * @param rfq the request for quote content
     * @param sent sent or received
     */
    protected void requestForQuoteTimeout(final RequestForQuote rfq, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(rfq.getUniqueId()))
        {
            this.removeMessage(rfq, sent);
            super.removeInternalDemand(rfq.getInternalDemandId());
        }
    }

    /**
     * @param quote the quote content
     * @param sent sent or received
     */
    protected void quoteTimeout(final Quote quote, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(quote.getUniqueId()))
        {
            this.removeMessage(quote, sent);
            this.removeMessage(quote.getRequestForQuote(), !sent);
            super.removeInternalDemand(quote.getInternalDemandId());
        }
    }

    /**
     * @param order the quote based order content
     * @param sent sent or received
     */
    protected void orderBasedOnQuoteTimeout(final OrderBasedOnQuote order, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(order.getUniqueId()))
        {
            this.removeMessage(order, sent);
            this.removeMessage(order.getQuote(), !sent);
            this.removeMessage(order.getQuote().getRequestForQuote(), sent);
            super.removeInternalDemand(order.getInternalDemandId());
        }
    }

    /**
     * @param order the stand alone order content
     * @param sent sent or received
     */
    protected void orderStandAloneTimeout(final OrderStandalone order, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(order.getUniqueId()))
        {
            this.removeMessage(order, sent);
            super.removeInternalDemand(order.getInternalDemandId());
        }
    }
}
