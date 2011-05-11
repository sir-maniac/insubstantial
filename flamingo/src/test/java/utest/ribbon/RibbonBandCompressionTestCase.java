package utest.ribbon;

import org.fest.assertions.Assertions;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.JRibbonFrame;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.IconRibbonBandResizePolicy;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;
import org.pushingpixels.flamingo.internal.ui.ribbon.BasicRibbonBandUI;
import test.svg.transcoded.edit_copy;
import test.svg.transcoded.edit_cut;
import test.svg.transcoded.edit_paste;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RibbonBandCompressionTestCase extends
        FestSwingJUnitTestCase {
    JRibbonFrame ribbonFrame;

    JRibbonBand ribbonBand1;

    JRibbonBand ribbonBand2;

    private static JRibbonBand getSampleRibbonBand(
            ResizableIcon icon) {
        JRibbonBand clipboardBand = new JRibbonBand("Clipboard",
                icon);

        JCommandButton mainButton = new JCommandButton("Paste",
                new edit_paste());
        mainButton
                .setCommandButtonKind(JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION);
        clipboardBand.addCommandButton(mainButton, RibbonElementPriority.TOP);

        JCommandButton cutButton = new JCommandButton("Cut", new edit_cut());
        cutButton
                .setCommandButtonKind(JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION);
        clipboardBand.addCommandButton(cutButton, RibbonElementPriority.MEDIUM);

        JCommandButton copyButton = new JCommandButton("Copy", new edit_copy());
        copyButton
                .setCommandButtonKind(JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_POPUP);
        clipboardBand
                .addCommandButton(copyButton, RibbonElementPriority.MEDIUM);

        JCommandButton formatButton = new JCommandButton("Format",
                new edit_paste());
        formatButton
                .setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
        clipboardBand.addCommandButton(formatButton,
                RibbonElementPriority.MEDIUM);

        List<RibbonBandResizePolicy> resizePolicies = new ArrayList<RibbonBandResizePolicy>();
        resizePolicies.add(new IconRibbonBandResizePolicy(clipboardBand
                .getControlPanel()));
        clipboardBand.setResizePolicies(resizePolicies);

        return clipboardBand;
    }

    @Override
    protected void onSetUp() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {

                ribbonFrame = new JRibbonFrame();

                ribbonBand1 = getSampleRibbonBand(null);
                ribbonBand2 = getSampleRibbonBand(new edit_cut());
                ribbonFrame.getRibbon().addTask(
                        new RibbonTask("Task", ribbonBand1, ribbonBand2));

                Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getMaximumWindowBounds();
                ribbonFrame.setPreferredSize(new Dimension(r.width,
                        r.height / 2));
                ribbonFrame.pack();
                ribbonFrame.setLocation(r.x, r.y);
                ribbonFrame.setVisible(true);
                ribbonFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
    }

    @Test
    public void testDefaultActionListeners() {
        AbstractCommandButton collapsedButton1 = GuiActionRunner
                .execute(new GuiQuery<AbstractCommandButton>() {
                    @Override
                    protected AbstractCommandButton executeInEDT()
                            throws Throwable {
                        return ((BasicRibbonBandUI) ribbonBand1.getUI())
                                .getCollapsedButton();
                    }
                });
        robot().waitForIdle();

        Assertions.assertThat(collapsedButton1.getIcon()).isNull();

        AbstractCommandButton collapsedButton2 = GuiActionRunner
                .execute(new GuiQuery<AbstractCommandButton>() {
                    @Override
                    protected AbstractCommandButton executeInEDT()
                            throws Throwable {
                        return ((BasicRibbonBandUI) ribbonBand2.getUI())
                                .getCollapsedButton();
                    }
                });
        robot().waitForIdle();

        Assertions.assertThat(collapsedButton2.getIcon()).isInstanceOf(edit_cut.class);
    }

    @Test
    public void testSwitchFromNoIcon() {
        AbstractCommandButton collapsedButton1 = GuiActionRunner
                .execute(new GuiQuery<AbstractCommandButton>() {
                    @Override
                    protected AbstractCommandButton executeInEDT()
                            throws Throwable {
                        return ((BasicRibbonBandUI) ribbonBand1.getUI())
                                .getCollapsedButton();
                    }
                });
        robot().waitForIdle();

        Assertions.assertThat(collapsedButton1.getIcon()).isNull();

        // set a collapsed icon on the first ribbon band
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT()
                    throws Throwable {
                ribbonBand1.setIcon(new edit_paste());
            }
        });
        robot().waitForIdle();

        collapsedButton1 = GuiActionRunner
                .execute(new GuiQuery<AbstractCommandButton>() {
                    @Override
                    protected AbstractCommandButton executeInEDT()
                            throws Throwable {
                        return ((BasicRibbonBandUI) ribbonBand1.getUI())
                                .getCollapsedButton();
                    }
                });
        robot().waitForIdle();

        Assertions.assertThat(collapsedButton1.getIcon()).isInstanceOf(edit_paste.class);
    }

    @Test
    public void testSwitchToNoIcon() {
        AbstractCommandButton collapsedButton2 = GuiActionRunner
                .execute(new GuiQuery<AbstractCommandButton>() {
                    @Override
                    protected AbstractCommandButton executeInEDT()
                            throws Throwable {
                        return ((BasicRibbonBandUI) ribbonBand2.getUI())
                                .getCollapsedButton();
                    }
                });
        robot().waitForIdle();

        Assertions.assertThat(collapsedButton2.getIcon()).isNotNull();

        // set a null collapse icon on the second ribbon band
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                ribbonBand2.setIcon(null);
            }
        });
        robot().waitForIdle();

        collapsedButton2 = GuiActionRunner
                .execute(new GuiQuery<AbstractCommandButton>() {
                    @Override
                    protected AbstractCommandButton executeInEDT()
                            throws Throwable {
                        return ((BasicRibbonBandUI) ribbonBand2.getUI())
                                .getCollapsedButton();
                    }
                });
        robot().waitForIdle();

        Assertions.assertThat(collapsedButton2.getIcon()).isNull();
    }

    @Test
    public void testSwitchToAnotherIcon() {
        AbstractCommandButton collapsedButton2 = GuiActionRunner
                .execute(new GuiQuery<AbstractCommandButton>() {
                    @Override
                    protected AbstractCommandButton executeInEDT()
                            throws Throwable {
                        return ((BasicRibbonBandUI) ribbonBand2.getUI())
                                .getCollapsedButton();
                    }
                });
        robot().waitForIdle();

        Assertions.assertThat(collapsedButton2).isNotNull();

        // set another collapse icon on the second ribbon band
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                ribbonBand2.setIcon(new edit_paste());
            }
        });
        robot().waitForIdle();

        collapsedButton2 = GuiActionRunner
                .execute(new GuiQuery<AbstractCommandButton>() {
                    @Override
                    protected AbstractCommandButton executeInEDT()
                            throws Throwable {
                        return ((BasicRibbonBandUI) ribbonBand2.getUI())
                                .getCollapsedButton();
                    }
                });
        robot().waitForIdle();

        Assertions.assertThat(collapsedButton2.getIcon()).isInstanceOf(edit_paste.class);

    }

}
