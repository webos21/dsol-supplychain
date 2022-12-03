package nl.tudelft.simulation.supplychain.policy.yp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.djunits.unit.LengthUnit;
import org.djunits.value.vdouble.scalar.Length;
import org.djutils.draw.point.Point3d;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.jstats.distributions.unit.DistContinuousDuration;
import nl.tudelft.simulation.supplychain.actor.SupplyChainActor;
import nl.tudelft.simulation.supplychain.message.Message;
import nl.tudelft.simulation.supplychain.message.trade.TradeMessageTypes;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageAnswer;
import nl.tudelft.simulation.supplychain.message.trade.YellowPageRequest;
import nl.tudelft.simulation.supplychain.policy.SupplyChainPolicy;
import nl.tudelft.simulation.supplychain.yellowpage.YellowPage;

/**
 * The YellowPageRequestHandler implements the business logic for a yellow page actor who receives a YellowPageRequest and has
 * to look up supply chain actors within the boundaries of the request For the moment, these are max number, max distance, and
 * product.
 * <p>
 * Copyright (c) 2003-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved.
 * <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class YellowPageRequestPolicy extends SupplyChainPolicy
{
    /** the serial version uid. */
    private static final long serialVersionUID = 12L;

    /** the handling time of the handler in simulation time units. */
    private DistContinuousDuration handlingTime;

    /**
     * Constructs a new YellowPageRequestHandler.
     * @param owner SupplyChainActor; the owner of the policy
     * @param handlingTime the distribution of the time to react on the YP request
     */
    public YellowPageRequestPolicy(final YellowPage owner, final DistContinuousDuration handlingTime)
    {
        super("YellowPageRequestPolicy", owner, TradeMessageTypes.YP_REQUEST);
        this.handlingTime = handlingTime;
    }

    /** {@inheritDoc} */
    @Override
    public boolean handleMessage(final Message message)
    {
        if (!isValidContent(message))
        {
            return false;
        }
        YellowPageRequest ypRequest = (YellowPageRequest) message;
        Set<SupplyChainActor> supplierSet = ((YellowPage) getOwner()).getSuppliers(ypRequest.getProduct());
        if (supplierSet == null)
        {
            Logger.warn("YellowPage '{}' has no supplier map for product {}", getOwner().getName(),
                    ypRequest.getProduct().getName());
            return false;
        }
        SortedMap<Length, SupplyChainActor> suppliers =
                pruneDistance(supplierSet, ypRequest.getMaximumDistance(), ypRequest.getSender().getLocation());
        pruneNumber(suppliers, ypRequest.getMaximumNumber());
        List<SupplyChainActor> potentialSuppliers = new ArrayList<>(suppliers.values());
        YellowPageAnswer ypAnswer = new YellowPageAnswer(getOwner(), ypRequest.getSender(), ypRequest.getInternalDemandId(),
                potentialSuppliers, ypRequest);
        getOwner().sendMessage(ypAnswer, this.handlingTime.draw());
        return true;
    }

    /**
     * Prune the list of suppliers based on the maximum distance.
     * @param supplierSet the set of suppliers
     * @param maxDistance the maximum distance tgo use for pruning
     * @param location the location to compare the supplier locations with
     * @return a map of suppliers, sorted on distance
     */
    private SortedMap<Length, SupplyChainActor> pruneDistance(final Set<SupplyChainActor> supplierSet, final Length maxDistance,
            final Point3d location)
    {
        SortedMap<Length, SupplyChainActor> sortedSuppliers = new TreeMap<>();
        Iterator<SupplyChainActor> i = supplierSet.iterator();
        while (i.hasNext())
        {
            SupplyChainActor actor = i.next();
            // TODO: get proper locations; assume km for now...
            Length distance = new Length(actor.getLocation().distance(location), LengthUnit.KILOMETER);
            if (distance.le(maxDistance))
            {
                sortedSuppliers.put(distance, actor);
            }
        }
        return sortedSuppliers;
    }

    /**
     * Prune the list of suppliers based on the number.
     * @param suppliers the map of suppliers (sorted on distance)
     * @param maxNumber the maximum number to leave
     */
    private void pruneNumber(final SortedMap<Length, SupplyChainActor> suppliers, final int maxNumber)
    {
        int count = 0;
        Iterator<SupplyChainActor> supplierIterator = suppliers.values().iterator();
        while (supplierIterator.hasNext())
        {
            supplierIterator.next();
            if (++count > maxNumber)
            {
                supplierIterator.remove();
            }
        }
    }

}
