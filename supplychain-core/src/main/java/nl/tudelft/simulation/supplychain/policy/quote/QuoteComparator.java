package nl.tudelft.simulation.supplychain.policy.quote;

import java.io.Serializable;
import java.util.Comparator;

import org.djunits.value.vdouble.scalar.Time;
import org.djutils.draw.point.Point2d;
import org.djutils.exceptions.Throw;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.supplychain.actor.Role;
import nl.tudelft.simulation.supplychain.finance.Money;
import nl.tudelft.simulation.supplychain.message.trade.Quote;

/**
 * Class for comparing quotes.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class QuoteComparator implements Comparator<Quote>, Serializable {
	/** the serial version uid. */
	private static final long serialVersionUID = 20221201L;

	/** comparatorType indicates the sorting order for the comparator. */
	private QuoteComparatorEnum comparatorType;

	/** ownerPosition stores the position of the owner. */
	private Point2d ownerPosition;

	/**
	 * @param owner          the supply chain actor
	 * @param comparatorType the type of comparator to use
	 */
	public QuoteComparator(final Role owner, final QuoteComparatorEnum comparatorType) {
		super();
		Throw.whenNull(owner, "owner cannot be null");
		Throw.whenNull(comparatorType, "comparatorType cannot be null");
		this.comparatorType = comparatorType;
		this.ownerPosition = (Point2d) owner.getActor().getLocation();
	}

	/** {@inheritDoc} */
	@Override
	public int compare(final Quote quote1, final Quote quote2) {
		Money price0 = quote1.getPrice();
		Money price1 = quote2.getPrice();
		int priceCompare = Double.compare(price0.getAmount(), price1.getAmount());
		Time date0 = quote1.getProposedDeliveryDate();
		Time date1 = quote2.getProposedDeliveryDate();
		int dateCompare = Double.compare(date0.si, date1.si);
		double distance0 = ((Point2d) quote1.getSender().getLocation()).distance(this.ownerPosition);
		double distance1 = ((Point2d) quote2.getSender().getLocation()).distance(this.ownerPosition);
		int distanceCompare = Double.compare(distance0, distance1);
		switch (this.comparatorType) {
		case SORT_DATE_DISTANCE_PRICE:
			if (dateCompare != 0) {
				return dateCompare;
			} else if (distanceCompare != 0) {
				return distanceCompare;
			} else {
				return priceCompare;
			}
		case SORT_DATE_PRICE_DISTANCE:
			if (dateCompare != 0) {
				return dateCompare;
			} else if (priceCompare != 0) {
				return priceCompare;
			} else {
				return distanceCompare;
			}
		case SORT_DISTANCE_DATE_PRICE:
			if (distanceCompare != 0) {
				return distanceCompare;
			} else if (dateCompare != 0) {
				return dateCompare;
			} else {
				return priceCompare;
			}
		case SORT_DISTANCE_PRICE_DATE:
			if (distanceCompare != 0) {
				return distanceCompare;
			} else if (priceCompare != 0) {
				return priceCompare;
			} else {
				return dateCompare;
			}
		case SORT_PRICE_DATE_DISTANCE:
			if (priceCompare != 0) {
				return priceCompare;
			} else if (dateCompare != 0) {
				return dateCompare;
			} else {
				return distanceCompare;
			}
		case SORT_PRICE_DISTANCE_DATE:
			if (priceCompare != 0) {
				return priceCompare;
			} else if (distanceCompare != 0) {
				return distanceCompare;
			} else {
				return dateCompare;
			}
		default:
			Logger.error("QuoteHandler$compare - Illegal comparator type=" + this.comparatorType);
			break;
		}
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.comparatorType.toString();
	}
}
