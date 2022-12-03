package nl.tudelft.simulation.supplychain.policy;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.MessageType;
import nl.tudelft.simulation.supplychain.message.policy.AbstractMessagePolicy;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessageTypes;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * SupplyChainHandler is the SupplyChainActor specific abstract Handler class. It has a SupplyChainActor as owner, making it
 * unnecessary to cast the Actor all the time to a SupplyChainActor. <br>
 * The generic SupplyChainHandler already has the methods to check whether the content is of the right type, and methods to do
 * basic filtering on product and on the partner with whom the owner is dealing. This makes it very easy to have different
 * handlers for e.g. production orders and for purchase orders; it can be done on the basis of the message sender (in case of
 * production orders the owner itself), or on the basis of the product type.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public abstract class SupplyChainPolicy extends AbstractMessagePolicy
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the products for which this handler is valid. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<Product> validProducts = new LinkedHashSet<Product>();

    /** the partner actors for which this handler is valid. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<SupplyChainActor> validPartners = new LinkedHashSet<SupplyChainActor>();

    /**
     * @param id String; the id of the policy
     * @param owner Actor; the owner of this policy
     * @param messageType MessageType; the message type that this policy can process
     */
    public SupplyChainPolicy(final String id, final SupplyChainActor owner, final MessageType messageType)
    {
        super(id, owner, messageType);
    }

    /**
     * Check Content in terms of class and owner.
     * @param message the content to check
     * @return returns whether the content is okay, and we are the one supposed to handle it
     */
    protected boolean checkMessage(final TradeMessage message)
    {
        if (!getMessageType().equals(message.getType()))
        {
            Logger.warn("checkContent - Wrong content type for actor " + getOwner() + ", handler " + this.getClass() + ": "
                    + message.getClass());
            return false;
        }
        if (!message.getReceiver().equals(getOwner()))
        {
            Logger.warn("checkContent - Wrong receiver for content " + message.toString() + " sent to actor " + getOwner());
            return false;
        }
        return true;
    }

    /**
     * Add a valid product to the list of products to handle with this handler.
     * @param product a new valid product to use
     */
    public void addValidProduct(final Product product)
    {
        this.validProducts.add(product);
    }

    /**
     * @return the valid products.
     */
    public Set<Product> getValidProducts()
    {
        return this.validProducts;
    }

    /**
     * Replace the current set of valid products. If you want to ADD a set, use addValidProduct per product instead.
     * @param validProducts A new set of valid products
     */
    public void setValidProducts(final Set<Product> validProducts)
    {
        this.validProducts = validProducts;
    }

    /**
     * Check whether the product is of the right type for this handler.
     * @param message the content to check
     * @return whether type is right or not
     */
    private boolean checkValidProduct(final TradeMessage message)
    {
        if (this.validProducts == null)
        {
            return true;
        }
        if (this.validProducts.size() == 0)
        {
            return true;
        }
        if (message instanceof InternalDemand)
        {
            return (this.validProducts.contains(((InternalDemand) (message)).getProduct()));
        }
        long id = message.getInternalDemandId();
        // get the internal demand to retrieve the product
        List<TradeMessage> storedIDs = getOwner().getMessageStore().getMessageList(id, TradeMessageTypes.INTERNAL_DEMAND);
        if (storedIDs.size() == 0)
        {
            return false;
        }
        InternalDemand internalDemand = (InternalDemand) storedIDs.get(0);
        return (this.validProducts.contains(internalDemand.getProduct()));
    }

    /** {@inheritDoc} */
    @Override
    public SupplyChainActor getOwner()
    {
        return (SupplyChainActor) super.getOwner();
    }

    /**
     * Add a valid partner to the list of supply chain partners to handle with this handler.
     * @param partner a new valid partner to use
     */
    public void addValidPartner(final SupplyChainActor partner)
    {
        this.validPartners.add(partner);
    }

    /**
     * @return the valid partners.
     */
    public Set<SupplyChainActor> getValidPartners()
    {
        return this.validPartners;
    }

    /**
     * Replace the current set of valid partners. If you want to ADD a set, use addValidPartner per partner instead.
     * @param validPartners A new set of valid partners.
     */
    public void setValidPartners(final Set<SupplyChainActor> validPartners)
    {
        this.validPartners = validPartners;
    }

    /**
     * Check whether the partner actor is one that this handler can handle.
     * @param content the content to check
     * @return whether partner is right or not
     */
    private boolean checkValidPartner(final TradeMessage content)
    {
        if (this.validPartners == null)
        {
            return true;
        }
        if (this.validPartners.size() == 0)
        {
            return true;
        }
        return (this.validPartners.contains(content.getSender()));
    }

    /**
     * Check partner and content for validity for this handler.
     * @param serContent the content to check
     * @return boolean indicating whether the content can be handled by this handler
     */
    protected boolean isValidContent(final Serializable serContent)
    {
        if (serContent == null || !(serContent instanceof TradeMessage))
        {
            Logger.warn("isValidContent", "Serializable content = null, or not of type Content");
            return false;
        }
        TradeMessage content = (TradeMessage) serContent;
        return checkMessage(content) && checkValidProduct(content) && checkValidPartner(content);
    }

}
