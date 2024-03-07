package nl.tudelft.simulation.supplychain.animation;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;

import org.djutils.draw.point.OrientedPoint3d;
import org.pmw.tinylog.Logger;

import nl.tudelft.simulation.dsol.animation.d2.Renderable2dInterface;
import nl.tudelft.simulation.dsol.swing.animation.d2.AnimationPanel;
import nl.tudelft.simulation.dsol.swing.animation.d2.InputListener;
import nl.tudelft.simulation.dsol.swing.introspection.gui.IntrospectionDialog;

/**
 * Editor for introspection.
 * <p>
 * Copyright (c) 2003-2023 Delft University of Technology, Delft, the
 * Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * 
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class MouseEditor extends InputListener {
	/**
	 * constructs a new MouseEditor.
	 * 
	 * @param panel the animation panel
	 */
	public MouseEditor(final AnimationPanel panel) {
		super(panel);
	}

	/**
	 * @param targets list of targets
	 */
	protected void edit(final List targets) {
		try {
			double zValue = -Double.MAX_VALUE;
			Renderable2dInterface selected = null;
			for (Iterator i = targets.iterator(); i.hasNext();) {
				Renderable2dInterface next = (Renderable2dInterface) i.next();
				double z = ((OrientedPoint3d) next.getSource().getLocation()).z;
				if (z > zValue) {
					zValue = z;
					selected = next;
				}
			}
			Object source = selected.getSource();
			if (source instanceof ContentAnimation) {
				source = ((ContentAnimation) source).getContent();
			}
			new IntrospectionDialog(source, source.toString());
		} catch (RemoteException exception) {
			Logger.warn(exception, "edit");
		}
	}
}
