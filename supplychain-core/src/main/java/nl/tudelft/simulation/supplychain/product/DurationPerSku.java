package nl.tudelft.simulation.supplychain.product;

import java.io.Serializable;
import java.util.Objects;

import org.djunits.Throw;
import org.djunits.value.vdouble.scalar.Duration;

/**
 * DurationPerSKU indicates a (handling) duration for, e.g., transloading or (un)loading of one SKU, independent of the product.
 * <p>
 * Copyright (c) 2022-2022 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class DurationPerSku implements Serializable
{
    /** */
    private static final long serialVersionUID = 20221202L;

    /** the SKU, independent of the product. */
    private final Sku sku;

    /** the (handling) duration for, e.g., transloading or (un)loading of one SKU, independent of the product. */
    private final Duration duration;

    /**
     * Construct a new DurationPerSKU object.
     * @param sku SKU; the SKU, independent of the product.
     * @param duration Duration; the (handling) duration for, e.g., transloading or (un)loading of one SKU, independent of the
     *            product.
     */
    public DurationPerSku(final Sku sku, final Duration duration)
    {
        Throw.whenNull(sku, "sku cannot be null");
        Throw.whenNull(duration, "duration cannot be null");
        this.sku = sku;
        this.duration = duration;
    }

    /**
     * Return the SKU, independent of the product.
     * @return sku SKU; the SKU, independent of the product
     */
    public Sku getSku()
    {
        return this.sku;
    }

    /**
     * Return the (handling) duration for, e.g., transloading or (un)loading of one SKU, independent of the product.
     * @return duration Duration; the (handling) duration of one SKU
     */
    public Duration getDuration()
    {
        return this.duration;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode()
    {
        return Objects.hash(this.duration, this.sku);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:needbraces")
    public boolean equals(final Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DurationPerSku other = (DurationPerSku) obj;
        return Objects.equals(this.duration, other.duration) && Objects.equals(this.sku, other.sku);
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "DurationPerSKU [sku=" + this.sku + ", duration=" + this.duration + "]";
    }

}
