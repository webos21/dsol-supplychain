package nl.tudelft.simulation.supplychain.transport;

import java.util.Set;

/**
 * TransportChoiceProvider chooses between options for transport.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public interface TransportChoiceProvider
{
    /**
     * Give the transport options for a transport from a sender actor to a receiver actor.
     * @param transportOptions set&lt;TransportOption&gt;; a set of transport options from sender to receiver
     * @return a preferred option for transport
     */
    TransportOption chooseTransportOptions(Set<TransportOption> transportOptions);

}
