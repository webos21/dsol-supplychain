package nl.tudelft.simulation.supplychain.message.store.trade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.djutils.event.EventProducer;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.MessageType;
import nl.tudelft.simulation.supplychain.message.trade.Bill;
import nl.tudelft.simulation.supplychain.message.trade.Order;
import nl.tudelft.simulation.supplychain.message.trade.OrderBasedOnQuote;
import nl.tudelft.simulation.supplychain.message.trade.OrderConfirmation;
import nl.tudelft.simulation.supplychain.message.trade.Payment;
import nl.tudelft.simulation.supplychain.message.trade.Quote;
import nl.tudelft.simulation.supplychain.message.trade.RequestForQuote;
import nl.tudelft.simulation.supplychain.message.trade.Shipment;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessageTypes;

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
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class TradeMessageStore extends EventProducer implements TradeMessageStoreInterface
{
    /** the serial version uid. */
    private static final long serialVersionUID = 20221203L;

    /** the received content. */
    private Map<Long, Map<MessageType, List<TradeMessage>>> internalDemandMap =
            Collections.synchronizedMap(new LinkedHashMap<>());

    /** the received content, latest state. */
    private Map<MessageType, List<TradeMessage>> receivedStateMap = Collections.synchronizedMap(new LinkedHashMap<>());

    /** the sent content, latest state. */
    private Map<MessageType, List<TradeMessage>> sentStateMap = Collections.synchronizedMap(new LinkedHashMap<>());

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
        long internalDemandId = message.getInternalDemandId();
        Map<MessageType, List<TradeMessage>> messageMap = this.internalDemandMap.get(internalDemandId);
        if (messageMap == null)
        {
            messageMap = new LinkedHashMap<MessageType, List<TradeMessage>>();
            this.internalDemandMap.put(internalDemandId, messageMap);
        }
        List<TradeMessage> messageList = messageMap.get(message.getType());
        if (messageList == null)
        {
            messageList = new ArrayList<TradeMessage>();
            messageMap.put(message.getType(), messageList);
        }
        messageList.add(message);
        MessageType messageType = foldExtendedMessageType(message.getType());
        Map<MessageType, List<TradeMessage>> srMap = sent ? this.sentStateMap : this.receivedStateMap;
        List<TradeMessage> srList = srMap.get(messageType);
        if (srList == null)
        {
            srList = new ArrayList<TradeMessage>();
            srMap.put(messageType, srList);
        }
        srList.add(message);
        removeOldStateContent(message, sent, internalDemandId);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void removeMessage(final TradeMessage message, final boolean sent)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        long identifier = message.getInternalDemandId();
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
    public synchronized void removeSentReceivedMessage(final TradeMessage message, final boolean sent)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        MessageType contentClass = foldExtendedMessageType(message.getType());
        Map<MessageType, List<TradeMessage>> srMap = sent ? this.sentStateMap : this.receivedStateMap;
        List<TradeMessage> srList = srMap.get(contentClass);
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
            removeMessageList(messageMap, TradeMessageTypes.YP_REQUEST);
            removeMessageList(messageMap, TradeMessageTypes.YP_ANSWER);
            removeMessageList(messageMap, TradeMessageTypes.RFQ);
            removeMessageList(messageMap, TradeMessageTypes.QUOTE);
            removeMessageList(messageMap, TradeMessageTypes.ORDER);
            removeMessageList(messageMap, TradeMessageTypes.ORDER_STANDALONE);
            removeMessageList(messageMap, TradeMessageTypes.ORDER_BASED_ON_QUOTE);
            removeMessageList(messageMap, TradeMessageTypes.ORDER_CONFIRMATION);
            removeMessageList(messageMap, TradeMessageTypes.SHIPMENT);
            removeMessageList(messageMap, TradeMessageTypes.BILL);
            removeMessageList(messageMap, TradeMessageTypes.PAYMENT);
            removeMessageList(messageMap, TradeMessageTypes.INTERNAL_DEMAND);
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
            while (messageList.size() > 0)
            {
                TradeMessage message = messageList.remove(0);
                this.removeMessage(message, true);
                this.removeMessage(message, false);
            }
        }
    }

    /**
     * When we do not have a pointer to the InternalDemand object, deleting an InternalDemand object is carried out through its
     * internalDemandId.
     * @param internalDemandId the identifier of the internal demand
     */
    protected void removeInternalDemand(final long internalDemandId)
    {
        Throw.whenNull(this.owner, "MessageStore - owner has not been initialized");
        Map<MessageType, List<TradeMessage>> idMap = null;
        idMap = this.internalDemandMap.remove(internalDemandId);

        if (idMap != null)
        {
            List<TradeMessage> messageList = idMap.get(TradeMessageTypes.INTERNAL_DEMAND);
            if (messageList != null)
            {
                for (TradeMessage message : messageList)
                {
                    this.removeMessage(message, true);
                    this.removeMessage(message, false);
                }
            }
        }
    }

    /**
     * Method getContentList returns a list of Content objects of type clazz based on the internalDemandId.
     * @param internalDemandId the identifier of the content
     * @param messageType the message type to look for
     * @return returns a list of content of type class based on the internalDemandId
     */
    @Override
    public List<TradeMessage> getMessageList(final long internalDemandId, final MessageType messageType)
    {
        List<TradeMessage> messageList = new ArrayList<>();
        for (TradeMessage message : this.internalDemandMap.get(internalDemandId).get(messageType))
        {
            messageList.add(message);
        }
        return messageList;
    }

    /** {@inheritDoc} */
    @Override
    public List<TradeMessage> getMessageList(final long internalDemandId, final MessageType messageType, final boolean sent)
    {
        MessageType type = foldExtendedMessageType(messageType);
        Map<MessageType, List<TradeMessage>> messageMap = sent ? this.sentStateMap : this.receivedStateMap;
        List<TradeMessage> messageList = messageMap.get(type);
        List<TradeMessage> result = new ArrayList<>();
        if (messageList != null)
        {
            for (TradeMessage m : messageList)
            {
                if (m.getInternalDemandId() == internalDemandId)
                {
                    result.add(m);
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
    @SuppressWarnings("checkstyle:methodlength")
    private void removeOldStateContent(final TradeMessage content, final boolean sent, final long internalDemandId)
    {
        // remove "old" data
        if (!sent && content instanceof Quote)
        {
            List<?> rfqList = getMessageList(internalDemandId, TradeMessageTypes.RFQ, true);
            if (rfqList.size() == 0)
            {
                Logger.warn(
                        "t=" + this.owner.getSimulatorTime() + " removeOldStateContent - could not find RFQ for quote uniqueId="
                                + content.getUniqueId() + ", IDid=" + content.getInternalDemandId() + " " + content.toString());
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
            List<?> quoteList = getMessageList(internalDemandId, TradeMessageTypes.QUOTE, false);
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
            List<?> orderList = getMessageList(internalDemandId, TradeMessageTypes.ORDER, true);
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
            List<?> orderConfirmationList = getMessageList(internalDemandId, TradeMessageTypes.ORDER_CONFIRMATION, false);
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
            List<?> billList = getMessageList(internalDemandId, TradeMessageTypes.BILL, false);
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
            List<?> rfqList = getMessageList(internalDemandId, TradeMessageTypes.RFQ, false);
            if (rfqList.size() == 0)
            {
                Logger.warn("t=" + this.owner.getSimulatorTime()
                        + " removeOldStateContent2 - could not find RFQ for quote uniqueId=" + content.getUniqueId() + ", IDid="
                        + content.getInternalDemandId() + " " + content.toString());
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
            List<?> quoteList = getMessageList(internalDemandId, TradeMessageTypes.QUOTE, true);
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
            List<?> orderList = getMessageList(internalDemandId, TradeMessageTypes.ORDER, false);
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
            List<?> orderConfirmationList = getMessageList(internalDemandId, TradeMessageTypes.ORDER_CONFIRMATION, true);
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
            List<?> billList = getMessageList(internalDemandId, TradeMessageTypes.BILL, true);
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
     * This method folds back extended message types onto their basic message type. Examples are OrderBasedOnQuote and
     * OrderStandAlone that are mapped back onto 'Order' to simplify the business logic, because the business logic now only has
     * to deal with an 'Order' in the MessageStore, and not with each of the separate extensions.
     * @param type MessageType; the message type of which to fold the message type
     * @return MessageType; the folded extended message type
     */
    protected MessageType foldExtendedMessageType(final MessageType type)
    {
        if (type.equals(TradeMessageTypes.ORDER_BASED_ON_QUOTE) || type.equals(TradeMessageTypes.ORDER_STANDALONE))
        {
            return TradeMessageTypes.ORDER;
        }
        return type;
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
        return this.owner.getName() + ".TradeMessageStore";
    }

}
