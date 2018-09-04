package nl.tudelft.simulation.supplychain.content;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.actor.Actor;
import nl.tudelft.simulation.dsol.formalisms.eventscheduling.SimEvent;
import nl.tudelft.simulation.dsol.simtime.TimeUnit;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.unit.simulator.DEVSSimulatorInterfaceUnit;

/**
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public class LeanContentStore extends ContentStore
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /** the simulator to schedule time-out events */
    protected DEVSSimulatorInterfaceUnit simulator;

    /** the map of unanswered content */
    private Map<Serializable, Content> unansweredContentMap = Collections.synchronizedMap(new HashMap<Serializable, Content>());

    /** the logger. */
    private static Logger logger = LogManager.getLogger(LeanContentStore.class);

    /**
     * @param owner the owner
     * @param simulator the simulator
     */
    public LeanContentStore(final SupplyChainActor owner, final DEVSSimulatorInterfaceUnit simulator)
    {
        super(owner);
        this.simulator = simulator;
    }

    /**
     * @see nl.tudelft.simulation.supplychain.content.ContentStore#addContent(nl.tudelft.simulation.supplychain.content.Content,
     *      boolean)
     */
    public synchronized void addContent(final Content content, final boolean sent)
    {
        super.addContent(content, sent);
        Class<?> contentClass = content.getClass();
        try
        {
            // schedule the removal after the 'lifetime' of the content is
            // unanswered
            if (InternalDemand.class.isAssignableFrom(contentClass))
            {
                InternalDemand internalDemand = (InternalDemand) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                Time date = Time.max(this.simulator.getSimulatorTime().get(), internalDemand.getLatestDeliveryDate());
                this.simulator.scheduleEventAbs(date, this, this, "internalDemandTimeout",
                        new Serializable[] { internalDemand, new Boolean(sent) });
            }
            else if (RequestForQuote.class.isAssignableFrom(contentClass) && !sent)
            {
                RequestForQuote rfq = (RequestForQuote) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                this.unansweredContentMap.remove(rfq.getInternalDemandID());
                Time date = Time.max(this.simulator.getSimulatorTime().get(), rfq.getCutoffDate());
                this.simulator.scheduleEventAbs(date, this, this, "requestForQuoteTimeout", new Serializable[] { rfq, new Boolean(sent) });
            }
            else if (RequestForQuote.class.isAssignableFrom(contentClass) && sent)
            {
                RequestForQuote rfq = (RequestForQuote) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                this.unansweredContentMap.remove(rfq.getInternalDemandID());
                Time date = Time.max(this.simulator.getSimulatorTime().get(),
                        rfq.getCutoffDate() + TimeUnit.convert(1.0, TimeUnit.DAY, this.simulator));
                this.simulator.scheduleEventAbs(date, this, this, "requestForQuoteTimeout", new Serializable[] { rfq, new Boolean(sent) });
            }
            else if (Quote.class.isAssignableFrom(contentClass))
            {
                Quote quote = (Quote) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                this.unansweredContentMap.remove(quote.getRequestForQuote().getUniqueID());
                double date = Math.max(quote.getProposedDeliveryDate(), quote.getRequestForQuote().getCutoffDate()
                        + TimeUnit.convert(1.0, TimeUnit.DAY, this.simulator));
                date = Math.max(date, quote.getRequestForQuote().getLatestDeliveryDate());
                date = Math.max(this.simulator.getSimulatorTime(), date);
                this.simulator.scheduleEventAbs(date, this, this, "quoteTimeout", new Serializable[] { quote, new Boolean(sent) });
            }
            else if (OrderBasedOnQuote.class.isAssignableFrom(contentClass))
            {
                OrderBasedOnQuote order = (OrderBasedOnQuote) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                this.unansweredContentMap.remove(order.getQuote().getUniqueID());
                double date = Math.max(order.getDeliveryDate(), order.getQuote().getProposedDeliveryDate());
                date = Math.max(date, order.getQuote().getRequestForQuote().getLatestDeliveryDate());
                date = Math.max(this.simulator.getSimulatorTime(), date);
                SimEvent event = new SimEvent(date, this, this, "orderBasedOnQuoteTimeout",
                        new Serializable[] { order, new Boolean(sent) });
                this.simulator.scheduleEvent(event);
            }
            else if (OrderStandAlone.class.isAssignableFrom(contentClass))
            {
                OrderStandAlone order = (OrderStandAlone) content;
                this.unansweredContentMap.put(content.getUniqueID(), content);
                this.unansweredContentMap.remove(order.getInternalDemandID());
                Time date = Time.max(this.simulator.getSimulatorTime().get(), order.getDeliveryDate());
                SimEvent event = new SimEvent(date, this, this, "orderStandAloneTimeout",
                        new Serializable[] { order, new Boolean(sent) });
                this.simulator.scheduleEvent(event);
            }
            else if (OrderConfirmation.class.isAssignableFrom(contentClass))
            {
                OrderConfirmation orderConfirmation = (OrderConfirmation) content;
                this.unansweredContentMap.remove(orderConfirmation.getOrder().getUniqueID());
            }
            else if (ProductionOrder.class.isAssignableFrom(contentClass) || Shipment.class.isAssignableFrom(contentClass)
                    || Bill.class.isAssignableFrom(contentClass) || Payment.class.isAssignableFrom(contentClass))
            {
                // nothing to do
            }
            else
            {
                logger.warn("addContent - could not find content class " + contentClass);
            }
        }
        catch (Exception e)
        {
            logger.warn("addContent", e);
        }
    }

    /**
     * @see nl.tudelft.simulation.supplychain.content.ContentStore#removeContent(nl.tudelft.simulation.supplychain.content.Content,
     *      boolean)
     */
    public synchronized void removeContent(final Content content, final boolean sent)
    {
        super.removeContent(content, sent);
        this.unansweredContentMap.remove(content.getUniqueID());
    }

    /**
     * @param internalDemand the internal demand
     * @param sent boolean sent
     */
    protected void internalDemandTimeout(final InternalDemand internalDemand, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(internalDemand.getUniqueID()))
        {
            this.removeContent(internalDemand, sent);
        }
    }

    /**
     * @param rfq the request for quote content
     * @param sent sent or received
     */
    protected void requestForQuoteTimeout(final RequestForQuote rfq, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(rfq.getUniqueID()))
        {
            this.removeContent(rfq, sent);
            super.removeInternalDemand(rfq.getInternalDemandID());
        }
    }

    /**
     * @param quote the quote content
     * @param sent sent or received
     */
    protected void quoteTimeout(final Quote quote, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(quote.getUniqueID()))
        {
            this.removeContent(quote, sent);
            this.removeContent(quote.getRequestForQuote(), !sent);
            super.removeInternalDemand(quote.getInternalDemandID());
        }
    }

    /**
     * @param order the quote based order content
     * @param sent sent or received
     */
    protected void orderBasedOnQuoteTimeout(final OrderBasedOnQuote order, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(order.getUniqueID()))
        {
            this.removeContent(order, sent);
            this.removeContent(order.getQuote(), !sent);
            this.removeContent(order.getQuote().getRequestForQuote(), sent);
            super.removeInternalDemand(order.getInternalDemandID());
        }
    }

    /**
     * @param order the stand alone order content
     * @param sent sent or received
     */
    protected void orderStandAloneTimeout(final OrderStandAlone order, final boolean sent)
    {
        if (this.unansweredContentMap.containsKey(order.getUniqueID()))
        {
            this.removeContent(order, sent);
            super.removeInternalDemand(order.getInternalDemandID());
        }
    }
}
