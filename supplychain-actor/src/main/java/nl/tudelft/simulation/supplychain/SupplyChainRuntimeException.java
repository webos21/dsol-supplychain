package nl.tudelft.simulation.supplychain;

/**
 * SupplyChainRuntimeException to indicate and create specific (silent) run time exceptions for the supply chain models.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SupplyChainRuntimeException extends RuntimeException
{
    /** */
    private static final long serialVersionUID = 20230407L;

    /**
     * Create the exception with a message.
     * @param message String; the message to explain the exception
     */
    public SupplyChainRuntimeException(final String message)
    {
        super(message);
    }

    /**
     * Wrap an earlier exception into this exception. 
     * @param cause Throwable; the earlier exception to wrap
     */
    public SupplyChainRuntimeException(final Throwable cause)
    {
        super(cause);
    }

    /**
     * Wrap an earlier exception into this exception, and give it a description.
     * @param message String; the message to explain the exception
     * @param cause Throwable; the earlier exception to wrap
     */
    public SupplyChainRuntimeException(final String message, final Throwable cause)
    {
        super(message, cause);
    }

}
