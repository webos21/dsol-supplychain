package nl.tudelft.simulation.supplychain.message.store.trade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.event.EventProducer;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.MessageType;
import nl.tudelft.simulation.supplychain.message.trade.Bill;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.Order;
import nl.tudelft.simulation.supplychain.message.trade.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.message.trade.OrderConfirmation;
import nl.tudelft.simulation.supplychain.message.trade.OrderStandalone;
import nl.tudelft.simulation.supplychain.message.trade.Payment;
import nl.tudelft.simulation.supplychain.message.trade.Quote;
import nl.tudelft.simulation.supplychain.message.trade.RequestForQuote;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;
import nl.tudelft.simulation.supplychain.message.trade.ShipmentQuality;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageRequest;

/**
 * The TradeMessageStore is taking care of storing messages for later use, for instance for matching purposes. It acts as a kind
 * of primitive database system. In this implementation, all the trade messages are linked to an InternalDemand, as this sets
 * off the whole chain of messages, no matter whether it is a purchase, internal production, or stock replenishment: in all
 * cases the InternalDemand triggers all the other messages. <br>
 * <br>
 * The MessageStore has a HashMap called internalDemandMap that maps the internal demand's internaldemandId onto the message
 * type map. This map has the Content's class as key, and maps that onto an ArrayList called 'messageList', which contains all
 * the contents sent or received in order of arrival or sending.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TradeMessageStore extends EventProducer implements TradeMessageStoreInterface
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the received content. */
    private Map<Long, Map<MessageType, List<Message>>> internalDemandMap = Collections.synchronizedMap(new LinkedHashMap<>());

    /** the received content, latest state. */
    private Map<MessageType, List<Message>> receivedStateMap = Collections.synchronizedMap(new LinkedHashMap<>());

    /** the sent content, latest state. */
    private Map<MessageType, List<Message>> sentStateMap = Collections.synchronizedMap(new LinkedHashMap<>());

    /** the owner. */
    private SupplyChainActor owner;

    /** {@inheritDoc} */
    @Override
    public void setOwner(final SupplyChainActor owner)
    {
        Throw.whenNull(owner, "owner cannot be null");
        Throw.when(this.owner != null, RuntimeException.class,
                "MessageStore - setting owner for %s while it has been set before", owner.toString());
        this.owner = owner;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void addMessage(final TradeMessage message, final boolean sent)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");

        long identifier = message.getInternalDemandId();
        // look if the internal demand already exists
        Map<MessageType, List<TradeMessage>> messageMap = this.internalDemandMap.get(identifier);
        if (messageMap == null)
        {
            messageMap = new LinkedHashMap<MessageType, List<TradeMessage>>();
            this.internalDemandMap.put(identifier, messageMap);
        }
        // look if the content class already exists in the messageMap
        List<TradeMessage> messageList = messageMap.get(message.getType());
        if (messageList == null)
        {
            messageList = new ArrayList<TradeMessage>();
            messageMap.put(message.getType(), messageList);
        }
        // add the new content to the end of the list
        messageList.add(message);
        //
        MessageType contentClass = foldExtendedContentClass(message);
        // look if the content class already exists
        Map<MessageType, List<TradeMessage>> srMap;
        if (sent)
        {
            srMap = this.sentStateMap;
        }
        else
        {
            srMap = this.receivedStateMap;
        }
        List<TradeMessage> srList = srMap.get(contentClass);
        if (srList == null)
        {
            srList = new ArrayList<TradeMessage>();
            srMap.put(contentClass, srList);
        }
        // add the new content to the end of the list
        srList.add(message);
        // old content...
        removeOldStateContent(message, sent, identifier);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeMessage(final TradeMessage message, final boolean sent)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");

        Serializable identifier = message.getInternalDemandId();
        // remove from InternalDemand map
        Map<MessageType, List<TradeMessage>> messageMap = this.internalDemandMap.get(identifier);
        if (messageMap != null)
        {
            List<TradeMessage> messageList = messageMap.get(message.getType());
            if (messageList != null)
            {
                messageList.remove(message);
            }
        }
        this.removeSentReceivedMessage(message, sent);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeSentReceivedMessage(final Message message, final boolean sent)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        MessageType contentClass = foldExtendedContentClass(message);
        Map<MessageType, List<TradeMessage>> srMap;
        if (sent)
        {
            srMap = this.sentStateMap;
        }
        else
        {
            srMap = this.receivedStateMap;
        }
        List<?> srList = srMap.get(contentClass);
        if (srList != null)
        {
            srList.remove(message);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllMessages(final long internalDemandId)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        Map<MessageType, List<TradeMessage>> messageMap = this.internalDemandMap.get(internalDemandId);
        if (messageMap != null)
        {
            removeMessageList(messageMap, YellowPageRequest.class);
            removeMessageList(messageMap, YellowPageAnswer.class);
            removeMessageList(messageMap, RequestForQuote.class);
            removeMessageList(messageMap, Quote.class);
            removeMessageList(messageMap, Order.class);
            removeMessageList(messageMap, OrderStandalone.class);
            removeMessageList(messageMap, OrderBasedOnQuote.class);
            removeMessageList(messageMap, OrderConfirmation.class);
            removeMessageList(messageMap, Shipment.class);
            removeMessageList(messageMap, Bill.class);
            removeMessageList(messageMap, Payment.class);
            removeMessageList(messageMap, InternalDemand.class);
        }
        removeInternalDemand(internalDemandId);
    }

    /**
     * Private, local method to remove all the content from one of the lists in the internalDemandMap for a certain
     * internalDemandId for a certain message type.
     * @param messageMap Map; the Map for one internal demand ID to clean
     * @param messageType MessageType; the message type to search for
     */
    private synchronized void removeMessageList(final Map<MessageType, List<TradeMessage>> messageMap,
            final MessageType messageType)
    {
        List<TradeMessage> messageList = messageMap.get(messageType);
        if (messageList != null)
        {
            int oldSize = messageList.size();
            while (messageList.size() > 0)
            {
                TradeMessage message = messageList.remove(0);
                this.removeMessage(message, true);
                this.removeMessage(message, false);
                if (oldSize == messageList.size())
                {
                    Logger.error("removeAllContent - object not removed from list for {}", messageType);
                    break;
                }
                oldSize = messageList.size();
            }
        }
    }

    /**
     * As we seldomly have a pointer to the InternalDemand object, deleting an InternalDemand object is carried out through its
     * ID.
     * @param internalDemandId the identifier of the internal demand
     */
    protected void removeInternalDemand(final long internalDemandId)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        Map<Long, TradeMessage idMap = null;
        idMap = this.internalDemandMap.remove(internalDemandId);

        if (idMap != null)
        {
            List<?> messageList = (List<?>) idMap.get(InternalDemand.class);
            if (messageList != null)
            {
                for (int i = 0; i < messageList.size(); i++)
                {
                    Message message = (Message) messageList.get(i);
                    this.removeMessage(message, true);
                    this.removeMessage(message, false);
                }
            }
        }
    }

    /**
     * Method getContentList returns a list of Content objects of type clazz based on the internalDemandId.
     * @param internalDemandId the identifier of the content
     * @param clazz the content class to look for
     * @return returns a list of content of type class based on the internalDemandId
     */
    @SuppressWarnings("unchecked")
    public <C extends Message> List<C> getContentList(final long internalDemandId, final Class<C> clazz)
    {
        List<C> messageList = new ArrayList<>();
        for (Message content : this.internalDemandMap.get(internalDemandId).get(clazz))
        {
            messageList.add((C) content);
        }
        return messageList;
    }

    /**
     * Method getContentList returns the Content object of type clazz based on the internalDemandId, for either sent or received
     * items.
     * @param internalDemandId the identifier of the content
     * @param clazz the content class to look for
     * @param sent indicates whether the content was sent or received
     * @return returns a list of content of type class based on the internalDemandId
     */
    @SuppressWarnings("unchecked")
    public <C extends Message> List<C> getContentList(final long internalDemandId, final Class<C> clazz, final boolean sent)
    {
        MessageType contentClass = clazz;
        if (clazz.equals(OrderBasedOnQuote.class) || clazz.equals(OrderStandalone.class))
        {
            contentClass = Order.class;
        }

        Map<MessageType, List<TradeMessage>> messageMap;
        if (sent)
        {
            messageMap = this.sentStateMap;
        }
        else
        {
            messageMap = this.receivedStateMap;
        }
        List<TradeMessage> messageList = messageMap.get(contentClass);
        List<C> result = new ArrayList<>();
        if (messageList != null)
        {
            Iterator<TradeMessage> it = messageList.iterator();
            while (it.hasNext())
            {
                Message itContent = it.next();
                if (itContent.getInternalDemandId() == internalDemandId)
                {
                    result.add((C) itContent);
                }
            }
        }
        return result;
    }

    /**
     * @param content the content to remove
     * @param sent indicates whether the content is sent or received
     * @param internalDemandId the internal demand id
     */
    private void removeOldStateContent(final Message content, final boolean sent, final long internalDemandId)
    {
        // remove "old" data
        if (!sent && content instanceof Quote)
        {
            List<?> rfqList = getContentList(internalDemandId, RequestForQuote.class, true);
            if (rfqList.size() == 0)
            {
                // TODO is this needed?
                if (TradeMessageStore.DEBUG)
                {
                    // only do this when debugging, otherwise during
                    // testing the error files grow extremely large
                    Logger.warn("t=" + this.owner.getSimulatorTime()
                            + " removeOldStateContent - could not find RFQ for quote uniqueId=" + content.getUniqueId()
                            + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
                }
            }
            else
            {
                for (int i = 0; i < rfqList.size(); i++)
                {
                    RequestForQuote rfq = (RequestForQuote) rfqList.get(i);
                    removeSentReceivedMessage(rfq, true);
                }
            }
        }
        else if (sent && content instanceof OrderBasedOnQuote)
        {
            List<?> quoteList = getContentList(internalDemandId, Quote.class, false);
            if (quoteList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find quote for order uniqueId=" + content.getUniqueId()
                        + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < quoteList.size(); i++)
                {
                    Quote quote = (Quote) quoteList.get(i);
                    removeSentReceivedMessage(quote, false);
                }
            }
        }
        else if (!sent && content instanceof OrderConfirmation)
        {
            List<?> orderList = getContentList(internalDemandId, Order.class, true);
            if (orderList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find order for order confirmation uniqueId="
                        + content.getUniqueId() + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < orderList.size(); i++)
                {
                    Order order = (Order) orderList.get(i);
                    removeSentReceivedMessage(order, true);
                }
            }
        }
        else if (!sent && content instanceof Shipment)
        {
            List<?> orderConfirmationList = getContentList(internalDemandId, OrderConfirmation.class, false);
            if (orderConfirmationList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find order confirmation for shipment uniqueId="
                        + content.getUniqueId() + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < orderConfirmationList.size(); i++)
                {
                    OrderConfirmation orderConfirmation = (OrderConfirmation) orderConfirmationList.get(i);
                    removeSentReceivedMessage(orderConfirmation, false);
                }
            }
        }
        else if (!sent && content instanceof Bill)
        {
            // don't do anything when the bill arrives
            // wait for payment to possibly clear some data
        }
        else if (sent && content instanceof Payment)
        {
            // remove the bill
            List<?> billList = getContentList(internalDemandId, Bill.class, false);
            if (billList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find bill for payment uniqueId=" + content.getUniqueId()
                        + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < billList.size(); i++)
                {
                    Bill bill = (Bill) billList.get(i);
                    removeSentReceivedMessage(bill, false);
                }
            }
        }

        // remove "old" data
        if (sent && content instanceof Quote)
        {
            List<?> rfqList = getContentList(internalDemandId, RequestForQuote.class, false);
            if (rfqList.size() == 0)
            {
                if (TradeMessageStore.DEBUG)
                {
                    // only do this when debugging, otherwise during
                    // testing the error files grow extremely large

                    Logger.warn("t=" + this.owner.getSimulatorTime()
                            + " removeOldStateContent2 - could not find RFQ for quote uniqueId=" + content.getUniqueId()
                            + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
                }
            }
            else
            {
                for (int i = 0; i < rfqList.size(); i++)
                {
                    RequestForQuote rfq = (RequestForQuote) rfqList.get(i);
                    removeSentReceivedMessage(rfq, false);
                }
            }
        }
        else if (!sent && content instanceof OrderBasedOnQuote)
        {
            List<?> quoteList = getContentList(internalDemandId, Quote.class, true);
            if (quoteList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find quote for order uniqueId=" + content.getUniqueId()
                        + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < quoteList.size(); i++)
                {
                    Quote quote = (Quote) quoteList.get(i);
                    removeSentReceivedMessage(quote, true);
                }
            }
        }
        else if (sent && content instanceof OrderConfirmation)
        {
            List<?> orderList = getContentList(internalDemandId, Order.class, false);
            if (orderList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find order for order confirmation uniqueId="
                        + content.getUniqueId() + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < orderList.size(); i++)
                {
                    Order order = (Order) orderList.get(i);
                    removeSentReceivedMessage(order, false);
                }
            }
        }
        else if (sent && content instanceof Shipment)
        {
            List<?> orderConfirmationList = getContentList(internalDemandId, OrderConfirmation.class, true);
            if (orderConfirmationList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find order confirmation for shipment uniqueId="
                        + content.getUniqueId() + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < orderConfirmationList.size(); i++)
                {
                    OrderConfirmation orderConfirmation = (OrderConfirmation) orderConfirmationList.get(i);
                    removeSentReceivedMessage(orderConfirmation, true);
                }
            }
        }
        else if (sent && content instanceof Bill)
        {
            // don't do anything with the bill yet
            // wait for payment
        }
        else if (!sent && content instanceof Payment)
        {
            // remove the bill
            List<?> billList = getContentList(internalDemandId, Bill.class, true);
            if (billList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent - could not find bill for payment uniqueId=" + content.getUniqueId()
                        + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
            }
            else
            {
                for (int i = 0; i < billList.size(); i++)
                {
                    Bill bill = (Bill) billList.get(i);
                    removeSentReceivedMessage(bill, true);
                }
            }
        }

    }

    /**
     * This method folds back extended content classes onto their basic class. Examples are OrderBasedOnQuote and
     * OrderStandAlone that are mapped back onto 'Order' to simplify the business logic, acause the business logic now only has
     * to deal with an 'Order' in the MessageStore, and not with each of the separate extensions.
     * @param content the content of which to fold the class
     * @return returns the class of the fold extended content class
     */
    protected MessageType foldExtendedContentClass(final Message content)
    {
        MessageType contentClass = content.getClass();
        if (contentClass.equals(OrderBasedOnQuote.class) || contentClass.equals(OrderStandalone.class))
        {
            contentClass = Order.class;
        }
        if (contentClass.equals(ShipmentQuality.class))
        {
            contentClass = Shipment.class;
        }
        return contentClass;
    }

    /**
     * @return the owner.
     */
    @Override
    public SupplyChainActor getOwner()
    {
        return this.owner;
    }

    /** {@inheritDoc} */
    @Override
    public Serializable getSourceId()
    {
        return this.owner.getName() + ".MessageStore";
    }

}
