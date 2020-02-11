package nl.tudelft.simulation.supplychain.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import javax.naming.NamingException;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.djunits.value.vdouble.scalar.Duration;
import org.djunits.value.vdouble.scalar.Time;

import nl.tudelft.simulation.dsol.SimRuntimeException;
import nl.tudelft.simulation.dsol.animation.Locatable;
import nl.tudelft.simulation.dsol.animation.D2.GisRenderable2D;
import nl.tudelft.simulation.dsol.experiment.Replication;
import nl.tudelft.simulation.dsol.experiment.ReplicationMode;
import nl.tudelft.simulation.dsol.model.DSOLModel;
import nl.tudelft.simulation.dsol.simulators.DEVSAnimator;
import nl.tudelft.simulation.dsol.simulators.SimulatorInterface;
import nl.tudelft.simulation.dsol.swing.animation.D2.AnimationPanel;
import nl.tudelft.simulation.jstats.streams.MersenneTwister;
import nl.tudelft.simulation.language.DSOLException;

/**
 * <p>
 * Copyright (c) 2013-2017 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="http://opentrafficsim.org/docs/license.html">OpenTrafficSim License</a>.
 * <p>
 * $LastChangedDate: 2018-02-16 12:36:12 +0100 (Fri, 16 Feb 2018) $, @version $Revision: 3796 $, by $Author: wjschakel $,
 * initial version Jun 18, 2015 <br>
 * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
 */
public abstract class AbstractWrappableAnimation implements WrappableAnimation, Serializable
{
    /** */
    private static final long serialVersionUID = 20150000L;

    /** Properties for the frame appearance (not simulation related). */
    protected Properties frameProperties;

    /** Use EXIT_ON_CLOSE when true, DISPOSE_ON_CLOSE when false on closing of the window. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected boolean exitOnClose;

    /** The tabbed panel so other tabs can be added by the classes that extend this class. */
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected SCAnimationPanel panel;

    /** Save the startTime for restarting the simulation. */
    private Time savedStartTime;

    /** Save the startTime for restarting the simulation. */
    private Duration savedWarmupPeriod;

    /** Save the runLength for restarting the simulation. */
    private Duration savedRunLength;

    /** The model. */
    private DSOLModel.TimeDoubleUnit model;

    /** Override the replication number by this value if non-null. */
    private Integer replication = 0;

    /** Current appearance. */
    private Appearance appearance = Appearance.GRAY;
    
    /** id. */
    private String id;

    /**
     * Build the animator.
     * @param id String; the id of the simulation
     * @param startTime Time; the start time
     * @param warmupPeriod Duration; the warm up period
     * @param runLength Duration; the duration of the simulation / animation
     * @param scModel OTSModelInterface; the simulation model
     * @return SimpleAnimator; a newly constructed animator
     * @throws SimRuntimeException on ???
     * @throws NamingException when context for the animation cannot be created
     */
    @SuppressWarnings("checkstyle:designforextension")
    protected DEVSAnimator.TimeDoubleUnit buildSimpleAnimator(final String id, final Time startTime,
        final Duration warmupPeriod, final Duration runLength, final DSOLModel.TimeDoubleUnit scModel)
        throws SimRuntimeException, NamingException
    {
        this.id = id;
        DEVSAnimator.TimeDoubleUnit animator = new DEVSAnimator.TimeDoubleUnit(id);
        animator.setPauseOnError(true);
        animator.setAnimationDelay(20); // 50 Hz animation update
        Replication.TimeDoubleUnit rep = Replication.TimeDoubleUnit.create("rep" + ++this.replication, startTime,
            warmupPeriod, runLength, scModel);
        animator.initialize(rep, ReplicationMode.TERMINATING);
        animator.getReplication().getExperiment().setSimulator(animator);
        rep.getStreams().put("default", new MersenneTwister(this.replication));
        return animator;
    }

    /**
     * Build the animator with the specified replication number.
     * @param startTime Time; the start time
     * @param warmupPeriod Duration; the warm up period
     * @param runLength Duration; the duration of the simulation / animation
     * @param scModel OTSModelInterface; the simulation model
     * @param replicationNumber int; the replication number
     * @return SimpleAnimator; a newly constructed animator
     * @throws SimRuntimeException on ???
     * @throws NamingException when context for the animation cannot be created
     */
    @SuppressWarnings("checkstyle:designforextension")
    protected DEVSAnimator.TimeDoubleUnit buildSimpleAnimator(final String id, final Time startTime,
        final Duration warmupPeriod, final Duration runLength, final DSOLModel.TimeDoubleUnit scModel,
        final int replicationNumber) throws SimRuntimeException, NamingException
    {
        DEVSAnimator.TimeDoubleUnit animator = new DEVSAnimator.TimeDoubleUnit(id);
        animator.setPauseOnError(true);
        animator.setAnimationDelay(20); // 50 Hz animation update
        Replication.TimeDoubleUnit rep = Replication.TimeDoubleUnit.create("rep" + replicationNumber, startTime,
            warmupPeriod, runLength, scModel);
        animator.initialize(rep, ReplicationMode.TERMINATING);
        animator.getReplication().getExperiment().setSimulator(animator);
        rep.getStreams().put("default", new MersenneTwister(replicationNumber));
        return animator;
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public DEVSAnimator.TimeDoubleUnit buildAnimator(final String id, final Time startTime, final Duration warmupPeriod,
        final Duration runLength, final Rectangle rect, final boolean eoc) throws SimRuntimeException, NamingException
    {
        this.exitOnClose = eoc;
        this.savedStartTime = startTime;
        this.savedWarmupPeriod = warmupPeriod;
        this.savedRunLength = runLength;
        this.model = makeModel();
        if (null == this.model)
        {
            return null; // Happens when the user cancels a file open dialog
        }

        // Animator
        final DEVSAnimator.TimeDoubleUnit simulator = null == this.replication ? buildSimpleAnimator(id, startTime,
            warmupPeriod, runLength, this.model) : buildSimpleAnimator(id, startTime, warmupPeriod, runLength, this.model,
                this.replication);
        try
        {
            this.panel = new SCAnimationPanel(new Rectangle2D.Double(0, 0, 100, 100), new Dimension(1024, 768), simulator,
                this);
        }
        catch (RemoteException | DSOLException exception)
        {
            throw new SimRuntimeException(exception);
        }

        // Case specific GUI elements
        addAnimationToggles();
        addTabs(simulator);

        // Frame
        SimulatorFrame frame = new SimulatorFrame(shortName(), this.panel);
        if (rect != null)
        {
            frame.setBounds(rect);
        }
        else
        {
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        frame.setDefaultCloseOperation(this.exitOnClose ? WindowConstants.EXIT_ON_CLOSE : WindowConstants.DISPOSE_ON_CLOSE);

        ////////////////////////////////////////
        ///// Look and Feel and Appearance /////
        ////////////////////////////////////////

        // Listener to write frame properties on frame close
        String sep = System.getProperty("file.separator");
        String propertiesFile = System.getProperty("user.home") + sep + "OTS" + sep + "properties.ini";
        frame.addWindowListener(new WindowAdapter()
        {
            /** {@inheritDoce} */
            @Override
            public void windowClosing(final WindowEvent windowEvent)
            {
                try
                {
                    File f = new File(propertiesFile);
                    f.getParentFile().mkdirs();
                    FileWriter writer = new FileWriter(f);
                    AbstractWrappableAnimation.this.frameProperties.store(writer, "OTS user settings");
                }
                catch (IOException exception)
                {
                    System.err.println("Could not store properties at " + propertiesFile + ".");
                }
            }
        });

        // Set default frame properties and load properties from file (if any)
        Properties defaults = new Properties();
        defaults.setProperty("Appearance", "GRAY");
        defaults.setProperty("LookAndFeel", "javax.swing.plaf.metal.MetalLookAndFeel");
        this.frameProperties = new Properties(defaults);
        try
        {
            FileReader reader = new FileReader(propertiesFile);
            this.frameProperties.load(reader);
        }
        catch (IOException ioe)
        {
            // ok, use defaults
        }
        this.appearance = Appearance.valueOf(this.frameProperties.getProperty("Appearance").toUpperCase());

        // Menu class to only accept the font of an Appearance
        class AppearanceControlMenu extends JMenu implements AppearanceControl
        {
            /** */
            private static final long serialVersionUID = 20180206L;

            public AppearanceControlMenu(final String string)
            {
                super(string);
            }

            /** {@inheritDoc} */
            @Override
            public boolean isFont()
            {
                return true;
            }
        }

        // Look and feel menu
        JMenu laf = new AppearanceControlMenu("Look and feel");
        laf.addMouseListener(new SubMenuShower(laf));
        ButtonGroup lafGroup = new ButtonGroup();
        lafGroup.add(addLookAndFeel(frame, laf, "javax.swing.plaf.metal.MetalLookAndFeel", "Metal"));
        lafGroup.add(addLookAndFeel(frame, laf, "com.sun.java.swing.plaf.motif.MotifLookAndFeel", "Motif"));
        lafGroup.add(addLookAndFeel(frame, laf, "javax.swing.plaf.nimbus.NimbusLookAndFeel", "Nimbus"));
        lafGroup.add(addLookAndFeel(frame, laf, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel", "Windows"));
        lafGroup.add(addLookAndFeel(frame, laf, "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel",
            "Windows classic"));
        lafGroup.add(addLookAndFeel(frame, laf, UIManager.getSystemLookAndFeelClassName(), "System default"));

        // Appearance menu
        JMenu app = new AppearanceControlMenu("Appearance");
        app.addMouseListener(new SubMenuShower(app));
        ButtonGroup appGroup = new ButtonGroup();
        for (Appearance appearanceValue : Appearance.values())
        {
            appGroup.add(addAppearance(app, appearanceValue));
        }

        // PopupMenu class to only accept the font of an Appearance
        class AppearanceControlPopupMenu extends JPopupMenu implements AppearanceControl
        {
            /** */
            private static final long serialVersionUID = 20180206L;

            /** {@inheritDoc} */
            @Override
            public boolean isFont()
            {
                return true;
            }
        }

        // Popup menu to change the Look and Feel or Appearance
        JPopupMenu popMenu = new AppearanceControlPopupMenu();
        popMenu.add(laf);
        popMenu.add(app);
        this.getPanel().getControlPanel().setComponentPopupMenu(popMenu);

        // Set the Look and Feel and Appearance as by frame properties
        setAppearance(getAppearance()); // color elements that were just added
        Try.execute(() -> UIManager.setLookAndFeel(this.frameProperties.getProperty("LookAndFeel")),
            "Could not set look-and-feel %s", laf);
        SwingUtilities.invokeLater(() -> SwingUtilities.updateComponentTreeUI(frame));

        return simulator;
    }

    /**
     * Adds a look-and-feel item.
     * @param frame JFrame; frame to set the look-and-feel to
     * @param group JMenu; menu to add item to
     * @param laf String; full path of LookAndFeel
     * @param name String; name on menu item
     * @return JMenuItem; menu item
     */
    private JCheckBoxMenuItem addLookAndFeel(final JFrame frame, final JMenu group, final String laf, final String name)
    {
        boolean checked = this.frameProperties.getProperty("LookAndFeel").equals(laf);
        JCheckBoxMenuItem check = new StayOpenCheckBoxMenuItem(name, checked);
        check.addMouseListener(new MouseAdapter()
        {
            /** {@inheritDoc} */
            @Override
            public void mouseClicked(final MouseEvent e)
            {
                Try.execute(() -> UIManager.setLookAndFeel(laf), "Could not set look-and-feel %s", laf);
                SwingUtilities.updateComponentTreeUI(frame);
                AbstractWrappableAnimation.this.frameProperties.setProperty("LookAndFeel", laf);
            }
        });
        group.add(check);
        return check;
    }

    /**
     * Adds an appearance to the menu.
     * @param group JMenu; menu to add item to
     * @param appear Appearance; appearance this item selects
     * @return JMenuItem; menu item
     */
    private JMenuItem addAppearance(final JMenu group, final Appearance appear)
    {
        JCheckBoxMenuItem check = new StayOpenCheckBoxMenuItem(appear.getName(), appear.equals(getAppearance()));
        check.addMouseListener(new MouseAdapter()
        {
            /** {@inheritDoc} */
            @Override
            public void mouseClicked(MouseEvent e)
            {
                setAppearance(appear);
            }
        });
        return group.add(check);
    }

    /**
     * Sets an appearance.
     * @param appearance Appearance; appearance
     */
    public void setAppearance(final Appearance appearance)
    {
        this.appearance = appearance;
        setAppearance(this.panel.getParent(), appearance);
        this.frameProperties.setProperty("Appearance", appearance.toString());
    }

    /**
     * Sets an appearance recursively on components.
     * @param c Component; visual component
     * @param appearance Appearance; look and feel
     */
    private void setAppearance(final Component c, final Appearance appearance)
    {
        if (c instanceof AppearanceControl)
        {
            AppearanceControl ac = (AppearanceControl) c;
            if (ac.isBackground())
            {
                c.setBackground(appearance.getBackground());
            }
            if (ac.isForeground())
            {
                c.setForeground(appearance.getForeground());
            }
            if (ac.isFont())
            {
                changeFont(c, appearance.getFont());
            }
        }
        else if (c instanceof AnimationPanel)
        {
            // animation backdrop
            c.setBackground(appearance.getBackdrop()); // not background
            c.setForeground(appearance.getForeground());
            changeFont(c, appearance.getFont());
        }
        else
        {
            // default
            c.setBackground(appearance.getBackground());
            c.setForeground(appearance.getForeground());
            changeFont(c, appearance.getFont());
        }
        if (c instanceof JSlider)
        {
            // labels of the slider
            Dictionary<?, ?> dictionary = ((JSlider) c).getLabelTable();
            Enumeration<?> keys = dictionary.keys();
            while (keys.hasMoreElements())
            {
                JLabel label = (JLabel) dictionary.get(keys.nextElement());
                label.setForeground(appearance.getForeground());
                label.setBackground(appearance.getBackground());
            }
        }
        // children
        if (c instanceof JComponent)
        {
            for (Component child : ((JComponent) c).getComponents())
            {
                setAppearance(child, appearance);
            }
        }
    }

    /**
     * Change font on component.
     * @param c Component; component
     * @param font String; font name
     */
    private void changeFont(final Component c, final String font)
    {
        Font prev = c.getFont();
        c.setFont(new Font(font, prev.getStyle(), prev.getSize()));
    }

    /**
     * Returns the appearance.
     * @return Appearance; appearance
     */
    public Appearance getAppearance()
    {
        return this.appearance;
    }

    /**
     * Make additional tabs in the main simulation window.
     * @param simulator SimpleSimulatorInterface; the simulator
     */
    protected void addTabs(final SimulatorInterface.TimeDoubleUnit simulator)
    {
        // Override this method to add custom tabs
    }

    /**
     * Placeholder method to place animation buttons or to show/hide classes on the animation.
     */
    @SuppressWarnings("checkstyle:designforextension")
    protected void addAnimationToggles()
    {
        // overridable placeholder to place animation buttons or to show/hide classes on the animation.
    }

    /**
     * Add a button for toggling an animatable class on or off. Button icons for which 'idButton' is true will be placed to the
     * right of the previous button, which should be the corresponding button without the id. An example is an icon for
     * showing/hiding the class 'Lane' followed by the button to show/hide the Lane ids.
     * @param name the name of the button
     * @param locatableClass the class for which the button holds (e.g., GTU.class)
     * @param iconPath the path to the 24x24 icon to display
     * @param toolTipText the tool tip text to show when hovering over the button
     * @param initiallyVisible whether the class is initially shown or not
     * @param idButton id button that needs to be placed next to the previous button
     */
    public final void addToggleAnimationButtonIcon(final String name, final Class<? extends Locatable> locatableClass,
        final String iconPath, final String toolTipText, final boolean initiallyVisible, final boolean idButton)
    {
        this.panel.addToggleAnimationButtonIcon(name, locatableClass, iconPath, toolTipText, initiallyVisible, idButton);
    }

    /**
     * Add a button for toggling an animatable class on or off.
     * @param name the name of the button
     * @param locatableClass the class for which the button holds (e.g., GTU.class)
     * @param toolTipText the tool tip text to show when hovering over the button
     * @param initiallyVisible whether the class is initially shown or not
     */
    public final void addToggleAnimationButtonText(final String name, final Class<? extends Locatable> locatableClass,
        final String toolTipText, final boolean initiallyVisible)
    {
        this.panel.addToggleAnimationButtonText(name, locatableClass, toolTipText, initiallyVisible);
    }

    /**
     * Set a class to be shown in the animation to true.
     * @param locatableClass the class for which the animation has to be shown.
     */
    public final void showAnimationClass(final Class<? extends Locatable> locatableClass)
    {
        this.panel.getAnimationPanel().showClass(locatableClass);
        this.panel.updateAnimationClassCheckBox(locatableClass);
    }

    /**
     * Set a class to be hidden in the animation to true.
     * @param locatableClass the class for which the animation has to be hidden.
     */
    public final void hideAnimationClass(final Class<? extends Locatable> locatableClass)
    {
        this.panel.getAnimationPanel().hideClass(locatableClass);
        this.panel.updateAnimationClassCheckBox(locatableClass);
    }

    /**
     * Toggle a class to be displayed in the animation to its reverse value.
     * @param locatableClass the class for which a visible animation has to be turned off or vice versa.
     */
    public final void toggleAnimationClass(final Class<? extends Locatable> locatableClass)
    {
        this.panel.getAnimationPanel().toggleClass(locatableClass);
        this.panel.updateAnimationClassCheckBox(locatableClass);
    }

    /**
     * Add a button for toggling a GIS class on or off.
     * @param header the name of the group of layers
     * @param gisMap the GIS map for which the toggles have to be added
     * @param toolTipText the tool tip text to show when hovering over the button
     */
    public final void addToggleGISButtonText(final String header, final GisRenderable2D gisMap, final String toolTipText)
    {
        this.panel.addToggleText(" ");
        this.panel.addToggleText(header);
        try
        {
            for (String layerName : gisMap.getMap().getLayerMap().keySet())
            {
                this.panel.addToggleGISButtonText(layerName, layerName, gisMap, toolTipText);
            }
        }
        catch (RemoteException exception)
        {
            exception.printStackTrace();
        }
    }

    /**
     * Set a GIS layer to be shown in the animation to true.
     * @param layerName the name of the GIS-layer that has to be shown.
     */
    public final void showGISLayer(final String layerName)
    {
        this.panel.showGISLayer(layerName);
    }

    /**
     * Set a GIS layer to be hidden in the animation to true.
     * @param layerName the name of the GIS-layer that has to be hidden.
     */
    public final void hideGISLayer(final String layerName)
    {
        this.panel.hideGISLayer(layerName);
    }

    /**
     * Toggle a GIS layer to be displayed in the animation to its reverse value.
     * @param layerName the name of the GIS-layer that has to be turned off or vice versa.
     */
    public final void toggleGISLayer(final String layerName)
    {
        this.panel.toggleGISLayer(layerName);
    }

    /**
     * @return the demo model. Don't forget to keep a local copy.
     */
    protected abstract DSOLModel.TimeDoubleUnit makeModel();

    /** {@inheritDoc} */
    @Override
    public final DEVSAnimator.TimeDoubleUnit rebuildSimulator(final Rectangle rect) throws SimRuntimeException,
        NamingException
    {
        return buildAnimator(getId(), this.savedStartTime, this.savedWarmupPeriod, this.savedRunLength, rect, this.exitOnClose);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("checkstyle:designforextension")
    public void stopTimersThreads()
    {
        if (this.panel != null && this.panel.getStatusBar() != null)
        {
            this.panel.getStatusBar().cancelTimer();
        }
        this.panel = null;
    }

    /**
     * @return panel
     */
    public final SCAnimationPanel getPanel()
    {
        return this.panel;
    }

    /**
     * Add a tab to the simulation window. This method can not be called from constructModel because the TabbedPane has not yet
     * been constructed at that time; recommended: override addTabs and call this method from there.
     * @param index int; index of the new tab; use <code>getTabCount()</code> to obtain the valid range
     * @param caption String; caption of the new tab
     * @param container Container; content of the new tab
     */
    public final void addTab(final int index, final String caption, final Container container)
    {
        this.panel.getTabbedPane().addTab(index, caption, container);
    }

    /**
     * Report the current number of tabs in the simulation window. This method can not be called from constructModel because the
     * TabbedPane has not yet been constructed at that time; recommended: override addTabs and call this method from there.
     * @return int; the number of tabs in the simulation window
     */
    public final int getTabCount()
    {
        return this.panel.getTabbedPane().getTabCount();
    }

    /** {@inheritDoc} */
    @Override
    public final void setNextReplication(final Integer nextReplication)
    {
        this.replication = nextReplication;
    }

    /**
     * @return id.
     */
    public String getId()
    {
        return this.id;
    }

    /**
     * Mouse listener which shows the submenu when the mouse enters the button.
     * <p>
     * Copyright (c) 2013-2017 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved.
     * <br>
     * BSD-style license. See <a href="http://opentrafficsim.org/node/13">OpenTrafficSim License</a>.
     * <p>
     * @version $Revision: 3796 $, $LastChangedDate: 2018-02-16 12:36:12 +0100 (Fri, 16 Feb 2018) $, by $Author: wjschakel $,
     *          initial version 6 feb. 2018 <br>
     * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
     * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
     */
    private class SubMenuShower extends MouseAdapter
    {
        /** The menu. */
        private JMenu menu;

        /**
         * Constructor.
         * @param menu JMenu; menu
         */
        public SubMenuShower(final JMenu menu)
        {
            this.menu = menu;
        }

        /** {@inheritDoc} */
        @Override
        public void mouseEntered(MouseEvent e)
        {
            MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[] {(MenuElement) this.menu.getParent(),
                this.menu, this.menu.getPopupMenu()});
        }
    }

    /**
     * Check box item that keeps the popup menu visible after clicking, so the user can click and try some options.
     * <p>
     * Copyright (c) 2013-2017 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved.
     * <br>
     * BSD-style license. See <a href="http://opentrafficsim.org/node/13">OpenTrafficSim License</a>.
     * <p>
     * @version $Revision: 3796 $, $LastChangedDate: 2018-02-16 12:36:12 +0100 (Fri, 16 Feb 2018) $, by $Author: wjschakel $,
     *          initial version 6 feb. 2018 <br>
     * @author <a href="http://www.tbm.tudelft.nl/averbraeck">Alexander Verbraeck</a>
     * @author <a href="http://www.tudelft.nl/pknoppers">Peter Knoppers</a>
     * @author <a href="http://www.transport.citg.tudelft.nl">Wouter Schakel</a>
     */
    private static class StayOpenCheckBoxMenuItem extends JCheckBoxMenuItem implements AppearanceControl
    {
        /** */
        private static final long serialVersionUID = 20180206L;

        /** Stored selection path. */
        private static MenuElement[] path;

        {
            getModel().addChangeListener(new ChangeListener()
            {

                @Override
                public void stateChanged(ChangeEvent e)
                {
                    if (getModel().isArmed() && isShowing())
                    {
                        setPath(MenuSelectionManager.defaultManager().getSelectedPath());
                    }
                }
            });
        }

        /**
         * Sets the path.
         * @param path MenuElement[]; path
         */
        public static void setPath(final MenuElement[] path)
        {
            StayOpenCheckBoxMenuItem.path = path;
        }

        /**
         * Constructor.
         * @param text String; menu item text
         * @param selected boolean; if the item is selected
         */
        public StayOpenCheckBoxMenuItem(final String text, final boolean selected)
        {
            super(text, selected);
        }

        /** {@inheritDoc} */
        @Override
        public void doClick(int pressTime)
        {
            super.doClick(pressTime);
            for (MenuElement element : path)
            {
                if (element instanceof JComponent)
                {
                    ((JComponent) element).setVisible(true);
                }
            }
            JMenu menu = (JMenu) path[path.length - 3];
            MenuSelectionManager.defaultManager().setSelectedPath(new MenuElement[] {(MenuElement) menu.getParent(), menu,
                menu.getPopupMenu()});
        }

        /** {@inheritDoc} */
        @Override
        public boolean isFont()
        {
            return true;
        }
    }

}
