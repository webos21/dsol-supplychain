package nl.tudelft.simulation.supplychain.gui;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

/**
 * SCRunSpeedSliderPanel to replace RunSpeedSliderPanel because its constructor is not visible.
 * <p>
 * Copyright (c) 2022-2023 Delft University of Technology, Delft, the Netherlands. All rights reserved. <br>
 * The supply chain Java library uses a BSD-3 style license.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class SCRunSpeedSliderPanel extends JPanel
{
    /** */
    private static final long serialVersionUID = 20221123L;

    /** The JSlider that the user sees. */
    private final JSlider slider;

    /** The ratios used in each decade. */
    private final int[] ratios;

    /** The values at each tick. */
    private Map<Integer, Double> tickValues = new LinkedHashMap<>();

    /** the multiplication factor. */
    private final double factor;

    /**
     * Construct a new SCRunSpeedSliderPanel.
     * @param minimum double; the minimum value on the scale (the displayed scale may extend a little further than this value)
     * @param maximum double; the maximum value on the scale (the displayed scale may extend a little further than this value)
     * @param initialValue double; the initially selected value on the scale
     * @param factor double; the multiplicaton factor to use for the simulation time
     * @param ticksPerDecade int; the number of steps per decade
     * @param simulator DevsSimulatorInterface&lt;?, ?, ?&gt;; the simulator to change the speed of
     */
    public SCRunSpeedSliderPanel(final double minimum, final double maximum, final double initialValue, final double factor,
            final int ticksPerDecade, final DevsSimulatorInterface<?> simulator)
    {
        if (minimum <= 0 || minimum > initialValue || initialValue > maximum)
        {
            throw new RuntimeException("Bad (combination of) minimum, maximum and initialValue; "
                    + "(restrictions: 0 < minimum <= initialValue <= maximum)");
        }
        this.factor = factor;
        switch (ticksPerDecade)
        {
            case 1:
                this.ratios = new int[] {1};
                break;
            case 2:
                this.ratios = new int[] {1, 3};
                break;
            case 3:
                this.ratios = new int[] {1, 2, 5};
                break;
            default:
                throw new RuntimeException("Bad ticksPerDecade value (must be 1, 2 or 3)");
        }
        int minimumTick = (int) Math.floor(Math.log10(minimum / initialValue) * ticksPerDecade);
        int maximumTick = (int) Math.ceil(Math.log10(maximum / initialValue) * ticksPerDecade);
        this.slider = new JSlider(SwingConstants.HORIZONTAL, minimumTick, maximumTick + 1, 0);
        this.slider.setPreferredSize(new Dimension(350, 45));
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        for (int step = 0; step <= maximumTick; step++)
        {
            StringBuilder text = new StringBuilder();
            text.append(this.ratios[step % this.ratios.length]);
            for (int decade = 0; decade < step / this.ratios.length; decade++)
            {
                text.append("0");
            }
            this.tickValues.put(step, Double.parseDouble(text.toString()));
            labels.put(step, new JLabel(text.toString().replace("000", "K")));
            // System.out.println("Label " + step + " is \"" + text.toString() + "\"");
        }
        // Figure out the DecimalSymbol
        String decimalSeparator =
                "" + ((DecimalFormat) NumberFormat.getInstance()).getDecimalFormatSymbols().getDecimalSeparator();
        for (int step = -1; step >= minimumTick; step--)
        {
            StringBuilder text = new StringBuilder();
            text.append("0");
            text.append(decimalSeparator);
            for (int decade = (step + 1) / this.ratios.length; decade < 0; decade++)
            {
                text.append("0");
            }
            int index = step % this.ratios.length;
            if (index < 0)
            {
                index += this.ratios.length;
            }
            text.append(this.ratios[index]);
            labels.put(step, new JLabel(text.toString()));
            this.tickValues.put(step, Double.parseDouble(text.toString()));
            // System.out.println("Label " + step + " is \"" + text.toString() + "\"");
        }
        labels.put(maximumTick + 1, new JLabel("\u221E"));
        this.tickValues.put(maximumTick + 1, 1E9);
        this.slider.setLabelTable(labels);
        this.slider.setPaintLabels(true);
        this.slider.setPaintTicks(true);
        this.slider.setMajorTickSpacing(1);
        this.add(this.slider);
        /*- Uncomment to verify the stepToFactor method.
        for (int i = this.slider.getMinimum(); i <= this.slider.getMaximum(); i++)
        {
            System.out.println("pos=" + i + " value is " + stepToFactor(i));
        }
         */

        // initial value of simulation speed
        if (simulator instanceof DevsRealTimeAnimator)
        {
            DevsRealTimeAnimator<?> clock = (DevsRealTimeAnimator<?>) simulator;
            clock.setSpeedFactor(this.factor * SCRunSpeedSliderPanel.this.tickValues.get(this.slider.getValue()));
        }

        // adjust the simulation speed
        this.slider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(final ChangeEvent ce)
            {
                JSlider source = (JSlider) ce.getSource();
                if (!source.getValueIsAdjusting() && simulator instanceof DevsRealTimeAnimator)
                {
                    DevsRealTimeAnimator<?> clock = (DevsRealTimeAnimator<?>) simulator;
                    clock.setSpeedFactor(SCRunSpeedSliderPanel.this.factor
                            * ((SCRunSpeedSliderPanel) source.getParent()).getTickValues().get(source.getValue()));
                }
            }
        });
    }

    /**
     * Access to tickValues map from within the event handler.
     * @return Map&lt;Integer, Double&gt; the tickValues map of this TimeWarpPanel
     */
    protected Map<Integer, Double> getTickValues()
    {
        return this.tickValues;
    }

    /**
     * Convert a position on the slider to a factor.
     * @param step int; the position on the slider
     * @return double; the factor that corresponds to step
     */
    private double stepToFactor(final int step)
    {
        int index = step % this.ratios.length;
        if (index < 0)
        {
            index += this.ratios.length;
        }
        double result = this.ratios[index];
        // Make positive to avoid trouble with negative values that round towards 0 on division
        int power = (step + 1000 * this.ratios.length) / this.ratios.length - 1000; // This is ugly
        while (power > 0)
        {
            result *= 10;
            power--;
        }
        while (power < 0)
        {
            result /= 10;
            power++;
        }
        return result;
    }

    /**
     * Retrieve the current TimeWarp factor.
     * @return double; the current TimeWarp factor
     */
    public double getFactor()
    {
        return stepToFactor(this.slider.getValue());
    }

    /** {@inheritDoc} */
    @Override
    public String toString()
    {
        return "SCRunSpeedSliderPanel [speed=" + this.getFactor() + "]";
    }

    /**
     * Set the time warp factor to the best possible approximation of a given value.
     * @param speed double; the requested speed
     */
    public void setSpeed(final double speed)
    {
        int bestStep = -1;
        double bestError = Double.MAX_VALUE;
        double logOfFactor = Math.log(speed / this.factor);
        for (int step = this.slider.getMinimum(); step <= this.slider.getMaximum(); step++)
        {
            double ratio = getTickValues().get(step); // stepToFactor(step);
            double logError = Math.abs(logOfFactor - Math.log(ratio));
            if (logError < bestError)
            {
                bestStep = step;
                bestError = logError;
            }
        }
        // System.out.println("setSpeedfactor: factor is " + factor + ", best slider value is " + bestStep
        // + " current value is " + this.slider.getValue());
        if (this.slider.getValue() != bestStep)
        {
            this.slider.setValue(bestStep);
        }
    }
}
