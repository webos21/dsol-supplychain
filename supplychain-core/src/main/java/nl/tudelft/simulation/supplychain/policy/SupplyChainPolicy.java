package nl.tudelft.simulation.supplychain.policy;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.SupplyChainActorInterface;
import nl.tudelft.simulation.supplychain.message.policy.AbstractMessagePolicy;
import nl.tudelft.simulation.supplychain.message.trade.InternalDemand;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessage;
import nl.tudelft.simulation.supplychain.product.Product;

/**
 * SupplyChainPolicy is the SupplyChainActor specific MessagePolicy class. It has a SupplyChainActor as owner, making it
 * unnecessary to cast the Actor all the time to a SupplyChainActor. <br>
 * The abstract SupplyChainPolicy has the methods to check whether the content is of the right type, and methods to do basic
 * filtering on product and on the partner with whom the owner is dealing. This makes it very easy to have different policies
 * for e.g. production orders and for purchase orders; it can be done on the basis of the message sender (in case of production
 * orders the owner itself), or on the basis of the product type.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @param <T> The type of TradeMessage for which this policy applies
 */
public abstract class SupplyChainPolicy<T extends TradeMessage> extends AbstractMessagePolicy<T>
{
    /** */
    private static final long serialVersionUID = 1L;

    /** the products for which this policy is valid; if empty, all products are valid. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<Product> validProducts = new LinkedHashSet<Product>();

    /** the partner actors for which this policy is valid; if empty, all partners are valid. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Set<SupplyChainActorInterface> validPartners = new LinkedHashSet<>();

    /**
     * @param id String; the id of the policy
     * @param owner SupplyChainActorInterface; the owner of this policy
     * @param messageClass Class&lt;T&gt;; the message type that this policy can process
     */
    public SupplyChainPolicy(final String id, final SupplyChainActorInterface owner, final Class<T> messageClass)
    {
        super(id, owner, messageClass);
    }

    /**
     * Check Message in terms of class and owner.
     * @param message the message to check
     * @return returns whether the message is okay, and we are the one supposed to handle it
     */
    protected boolean checkMessage(final TradeMessage message)
    {
        if (!getMessageClass().equals(message.getClass()))
        {
            Logger.warn("checkContent - Wrong content type for actor " + getOwner() + ", policy " + this.getClass() + ": "
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
     * Add a valid product to the list of products to handle with this policy.
     * @param product product; a new valid product to add to the valid product set for this policy
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
     * @param validProducts Set&lt;Product&gt;; a new set of valid products
     */
    public void setValidProducts(final Set<Product> validProducts)
    {
        this.validProducts = validProducts;
    }

    /**
     * Check whether the product is of the right type for this policy. If the set is empty, all products are valid.
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
        List<InternalDemand> storedIDs = getOwner().getMessageStore().getMessageList(id, InternalDemand.class);
        if (storedIDs.size() == 0)
        {
            return false;
        }
        InternalDemand internalDemand = storedIDs.get(0);
        return (this.validProducts.contains(internalDemand.getProduct()));
    }

    /** {@inheritDoc} */
    @Override
    public SupplyChainActorInterface getOwner()
    {
        return (SupplyChainActorInterface) super.getOwner();
    }

    /**
     * Add a valid partner to the list of supply chain partners to handle with this policy.
     * @param partner a new valid partner to use
     */
    public void addValidPartner(final SupplyChainActorInterface partner)
    {
        this.validPartners.add(partner);
    }

    /**
     * @return the valid partners.
     */
    public Set<SupplyChainActorInterface> getValidPartners()
    {
        return this.validPartners;
    }

    /**
     * Replace the current set of valid partners. If you want to ADD a set, use addValidPartner per partner instead.
     * @param validPartners A new set of valid partners.
     */
    public void setValidPartners(final Set<SupplyChainActorInterface> validPartners)
    {
        this.validPartners = validPartners;
    }

    /**
     * Check whether the partner actor is one that this policy can handle.
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
     * Check partner and content for validity for this policy.
     * @param message TradeMessage; the messageto check
     * @return boolean; indicating whether the content can be handled by this policy
     */
    protected boolean isValidMessage(final TradeMessage message)
    {
        return checkMessage(message) && checkValidProduct(message) && checkValidPartner(message);
    }

}
