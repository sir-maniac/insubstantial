package utest.ribbon;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.fest.assertions.Assertions;
import org.fest.swing.core.MouseButton;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiTask;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;
import org.junit.Test;
import org.pushingpixels.flamingo.api.common.*;
import org.pushingpixels.flamingo.api.common.icon.*;
import org.pushingpixels.flamingo.api.common.popup.*;
import org.pushingpixels.flamingo.api.ribbon.*;
import org.pushingpixels.flamingo.api.ribbon.resize.*;
import org.pushingpixels.flamingo.internal.ui.ribbon.JRibbonTaskToggleButton;
import org.pushingpixels.flamingo.internal.utils.FlamingoUtilities;
import org.pushingpixels.flamingo.internal.utils.RenderingUtils;

import test.common.SamplePopupMenu;
import test.svg.transcoded.*;

public class RibbonMinimizedTestCase extends FestSwingJUnitTestCase {
    private JRibbonFrame ribbonFrame;

    private JRibbon ribbon;

    protected static class QuickStylesPanel extends JCommandButtonPanel {
        public QuickStylesPanel() {
            super(32);

            for (int groupIndex = 0; groupIndex < 4; groupIndex++) {
                String iconGroupName = "Styles " + groupIndex;
                this.addButtonGroup(iconGroupName, groupIndex);
                for (int i = 0; i < 15; i++) {
                    final int index = i;
                    ResizableIcon fontIcon = new font_x_generic();
                    ResizableIcon finalIcon = new DecoratedResizableIcon(
                            fontIcon,
                            new DecoratedResizableIcon.IconDecorator() {
                                @Override
                                public void paintIconDecoration(Component c,
                                                                Graphics g, int x, int y, int width,
                                                                int height) {
                                    Graphics2D g2d = (Graphics2D) g.create();
                                    g2d.setColor(Color.black);
                                    g2d
                                            .setFont(UIManager
                                                    .getFont("Label.font"));
                                    RenderingUtils.installDesktopHints(g2d);
                                    g2d.drawString("" + index, x + 2, y
                                            + height - 2);
                                    g2d.dispose();
                                }
                            });
                    JCommandToggleButton jrb = new JCommandToggleButton(null,
                            finalIcon);
                    jrb.setName("Group " + groupIndex + ", index " + i);
                    jrb.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            System.out.println("Invoked action on " + index);
                        }
                    });
                    jrb.setActionRichTooltip(new RichTooltip("Index " + i,
                            "Sample tooltip for " + i));
                    this.addButtonToLastGroup(jrb);
                }
            }
            this.setSingleSelectionMode(true);
        }
    }

    private JRibbonBand getClipboardBand() {
        JRibbonBand clipboardBand = new JRibbonBand("Clipboard",
                new edit_paste());
        clipboardBand.setExpandButtonKeyTip("FO");

        JCommandButton mainButton = new JCommandButton("Paste",
                new edit_paste());
        mainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Pasted!");
            }
        });
        mainButton.setPopupCallback(new PopupPanelCallback() {
            @Override
            public JPopupPanel getPopupPanel(JCommandButton commandButton) {
                return new SamplePopupMenu(commandButton
                        .getComponentOrientation());
            }
        });
        mainButton
                .setCommandButtonKind(JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION);
        RichTooltip mainRichTooltip = new RichTooltip();
        mainRichTooltip.setTitle("Paste");
        mainRichTooltip
                .addDescriptionSection("Paste the contents of the Clipboard");
        mainButton.setActionRichTooltip(mainRichTooltip);
        mainButton.setPopupKeyTip("V");

        RichTooltip mainPopupRichTooltip = new RichTooltip();
        mainPopupRichTooltip.setTitle("Paste");
        mainPopupRichTooltip
                .addDescriptionSection("Click here for more options such as pasting only the values or formatting");
        mainButton.setPopupRichTooltip(mainPopupRichTooltip);

        clipboardBand.addCommandButton(mainButton, RibbonElementPriority.TOP);

        JCommandButton cutButton = new JCommandButton("Cut", new edit_cut());
        cutButton.setPopupCallback(new PopupPanelCallback() {
            @Override
            public JPopupPanel getPopupPanel(JCommandButton commandButton) {
                return new SamplePopupMenu(commandButton
                        .getComponentOrientation());
            }
        });
        cutButton
                .setCommandButtonKind(JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_ACTION);
        RichTooltip cutRichTooltip = new RichTooltip();
        cutRichTooltip.setTitle("Cut");
        cutRichTooltip
                .addDescriptionSection("Cut the selection from the document and put it on the Clipboard");
        cutButton.setActionRichTooltip(cutRichTooltip);
        cutButton.setPopupKeyTip("X");

        clipboardBand.addCommandButton(cutButton, RibbonElementPriority.MEDIUM);

        JCommandButton copyButton = new JCommandButton("Copy", new edit_copy());
        copyButton.setPopupCallback(new PopupPanelCallback() {
            @Override
            public JPopupPanel getPopupPanel(JCommandButton commandButton) {
                return new SamplePopupMenu(commandButton
                        .getComponentOrientation());
            }
        });
        copyButton
                .setCommandButtonKind(JCommandButton.CommandButtonKind.ACTION_AND_POPUP_MAIN_POPUP);
        copyButton.setPopupKeyTip("C");

        clipboardBand
                .addCommandButton(copyButton, RibbonElementPriority.MEDIUM);

        JCommandButton formatButton = new JCommandButton("Format",
                new edit_paste());
        formatButton.setPopupCallback(new PopupPanelCallback() {
            @Override
            public JPopupPanel getPopupPanel(JCommandButton commandButton) {
                JCommandPopupMenu popupMenu = new JCommandPopupMenu(
                        new QuickStylesPanel(), 5, 3);
                JCommandMenuButton saveSelectionButton = new JCommandMenuButton(
                        "Save Selection", new EmptyResizableIcon(16));
                saveSelectionButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Save Selection activated");
                    }
                });
                saveSelectionButton.setActionKeyTip("SS");
                popupMenu.addMenuButton(saveSelectionButton);

                JCommandMenuButton clearSelectionButton = new JCommandMenuButton(
                        "Clear Selection", new EmptyResizableIcon(16));
                clearSelectionButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Clear Selection activated");
                    }
                });
                clearSelectionButton.setActionKeyTip("SC");
                popupMenu.addMenuButton(clearSelectionButton);

                popupMenu.addMenuSeparator();
                JCommandMenuButton applyStylesButton = new JCommandMenuButton(
                        "Apply Styles", new EmptyResizableIcon(16));
                applyStylesButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Apply Styles activated");
                    }
                });
                applyStylesButton.setActionKeyTip("SA");
                popupMenu.addMenuButton(applyStylesButton);
                return popupMenu;
            }
        });

        formatButton
                .setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
        // pasteButton.addPopupActionListener(new SamplePopupActionListener());
        formatButton
                .setCommandButtonKind(JCommandButton.CommandButtonKind.POPUP_ONLY);
        formatButton.setPopupKeyTip("FP");

        clipboardBand.addCommandButton(formatButton,
                RibbonElementPriority.MEDIUM);

        List<RibbonBandResizePolicy> resizePolicies = new ArrayList<RibbonBandResizePolicy>();
        resizePolicies.add(new CoreRibbonResizePolicies.Mirror(clipboardBand
                .getControlPanel()));
        resizePolicies.add(new CoreRibbonResizePolicies.Mid2Low(clipboardBand
                .getControlPanel()));
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
                ribbon = ribbonFrame.getRibbon();

                RibbonTask task = new RibbonTask("Task", getClipboardBand());

                ribbon.addTask(task);

                Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getMaximumWindowBounds();
                ribbonFrame.setPreferredSize(new Dimension(r.width, r.height / 2));
                ribbonFrame.pack();
                ribbonFrame.setLocation(r.x, r.y);
                ribbonFrame.setVisible(true);
                ribbonFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
    }

    public void assertTrue(boolean bool) {
        Assertions.assertThat(bool).isTrue();
    }

    public void assertFalse(boolean bool) {
        Assertions.assertThat(bool).isFalse();
    }

    public void assertNotNull(Object obj) {
        Assertions.assertThat(obj).isNotNull();
    }

    @Test
    public void minimizeWithAPI() throws Exception {
        assertFalse(ribbon.isMinimized());

        minimizeRibbonViaAPI();
        assertTrue(ribbon.isMinimized());
    }

    private void minimizeRibbonViaAPI() {
        GuiActionRunner.execute(new GuiTask() {
            @Override
            protected void executeInEDT() throws Throwable {
                ribbon.setMinimized(true);
            }
        });
    }

    @Test
    public void minimizeWithKeyboard() throws Exception {
        assertFalse(ribbon.isMinimized());

        // wait between events to allow the Ctrl+F1 to be handled by Swing
        // and get to the ribbon
        robot().settings().delayBetweenEvents(300);
        robot().pressKey(KeyEvent.VK_CONTROL);
        robot().pressKey(KeyEvent.VK_F1);
        robot().releaseKey(KeyEvent.VK_F1);
        robot().releaseKey(KeyEvent.VK_CONTROL);
        assertTrue(ribbon.isMinimized());
    }

    @Test
    public void minimizeWithMouse() throws Exception {
        assertFalse(ribbon.isMinimized());

        JRibbonTaskToggleButton taskButton = getTaskButton(ribbon, "Task");
        assertNotNull(taskButton);

        // set enough delay to emulate double mouse click
        robot().settings().delayBetweenEvents(20);
        // move the mouse to the center of the task toggle button
        Point taskButtonLoc = taskButton.getLocationOnScreen();
        robot().moveMouse(taskButtonLoc.x + taskButton.getWidth() / 2,
                taskButtonLoc.y + taskButton.getHeight() / 2);
        // emulate double click
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        robot().waitForIdle();
        assertTrue(ribbon.isMinimized());
    }

    @Test
    public void minimizeAndPopup() throws Exception {
        minimizeRibbonViaAPI();

        JRibbonTaskToggleButton taskButton = getTaskButton(ribbon, "Task");
        assertNotNull(taskButton);

        // move the mouse to the center of the task toggle button
        Point taskButtonLoc = taskButton.getLocationOnScreen();
        robot().moveMouse(taskButtonLoc.x + taskButton.getWidth() / 2,
                taskButtonLoc.y + taskButton.getHeight() / 2);

        // mouse press should show the ribbon in popup
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        robot().waitForIdle();
        assertTrue(ribbon.isMinimized());
        // this is a variance from Office 2007, Flamingo registers on click, office on mouse down
//        assertTrue(FlamingoUtilities
//                .isShowingMinimizedRibbonInPopup(ribbon));

        // mouse release should not affect the state of the ribbon
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        assertTrue(ribbon.isMinimized());
        assertTrue(FlamingoUtilities
                .isShowingMinimizedRibbonInPopup(ribbon));

        Thread.sleep(500); // we have to out wait the double click

        // mouse press should hide the ribbon in popup
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        robot().waitForIdle();
        assertTrue(ribbon.isMinimized());
        // this is a variance from Office 2007, Flamingo registers on click, office on mouse down
//        assertFalse(FlamingoUtilities
//                .isShowingMinimizedRibbonInPopup(ribbon));

        // mouse release should not affect the state of the ribbon
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        robot().waitForIdle();
        assertTrue(ribbon.isMinimized());
        assertFalse(FlamingoUtilities
                .isShowingMinimizedRibbonInPopup(ribbon));

    }

    private static JRibbonTaskToggleButton getTaskButton(Component c,
                                                         String title) {
        if (c instanceof JRibbonTaskToggleButton) {
            if (title.equals(((JRibbonTaskToggleButton) c).getText()))
                return (JRibbonTaskToggleButton) c;
        }

        if (c instanceof Container) {
            Container cont = (Container) c;
            for (int i = 0; i < cont.getComponentCount(); i++) {
                JRibbonTaskToggleButton result = getTaskButton(cont
                        .getComponent(i), title);
                if (result != null)
                    return result;
            }
        }

        return null;
    }

    @Test
    public void commandButtonPopupInMinimizedRibbon() throws Exception {
        minimizeRibbonViaAPI();

        JRibbonTaskToggleButton taskButton = getTaskButton(ribbon, "Task");
        assertNotNull(taskButton);

        // move the mouse to the center of the task toggle button
        Point taskButtonLoc = taskButton.getLocationOnScreen();
        robot().moveMouse(taskButtonLoc.x + taskButton.getWidth() / 2,
                taskButtonLoc.y + taskButton.getHeight() / 2);

        // mouse press should show the ribbon in popup
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        Thread.sleep(200);
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        robot().waitForIdle();
        assertTrue(ribbon.isMinimized());
        assertTrue(FlamingoUtilities
                .isShowingMinimizedRibbonInPopup(ribbon));

        List<PopupPanelManager.PopupInfo> popups = PopupPanelManager
                .defaultManager().getShownPath();
        assertTrue(popups.size() > 0);

        JPopupPanel currPopupPanel = popups.get(popups.size() - 1)
                .getPopupPanel();
        JCommandButton cutButton = getCommandButton(currPopupPanel, "Cut");
        assertNotNull(cutButton);

        Point cutButtonLoc = cutButton.getLocationOnScreen();
        Rectangle cutPopupArea = cutButton.getUI().getLayoutInfo().popupClickArea;

        // bring the popup of the cut button
        robot().moveMouse(cutButtonLoc.x + cutPopupArea.x + cutPopupArea.width
                / 2, cutButtonLoc.y + cutPopupArea.y + cutPopupArea.height / 2);
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        Thread.sleep(100);
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        // ribbon should be minimized
        assertTrue(ribbon.isMinimized());
        // and showing in popup
        assertTrue(FlamingoUtilities
                .isShowingMinimizedRibbonInPopup(ribbon));
        // and cut button should show its popup
        assertTrue(cutButton.getPopupModel().isPopupShowing());

        // click in the popup area once again
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        Thread.sleep(100);
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        // ribbon should be minimized
        assertTrue(ribbon.isMinimized());
        // and showing in popup
        assertTrue(FlamingoUtilities
                .isShowingMinimizedRibbonInPopup(ribbon));
        // and cut button should not show its popup
        assertFalse(cutButton.getPopupModel().isPopupShowing());
    }

    @Test
    public void commandButtonPopupInMinimizedRibbon2() throws Exception {
        minimizeRibbonViaAPI();

        JRibbonTaskToggleButton taskButton = getTaskButton(ribbon, "Task");
        assertNotNull(taskButton);

        // move the mouse to the center of the task toggle button
        Point taskButtonLoc = taskButton.getLocationOnScreen();
        robot().moveMouse(taskButtonLoc.x + taskButton.getWidth() / 2,
                taskButtonLoc.y + taskButton.getHeight() / 2);

        // mouse press should show the ribbon in popup
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        Thread.sleep(200);
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        robot().waitForIdle();
        assertTrue(ribbon.isMinimized());
        assertTrue(FlamingoUtilities
                .isShowingMinimizedRibbonInPopup(ribbon));

        List<PopupPanelManager.PopupInfo> popups = PopupPanelManager
                .defaultManager().getShownPath();
        assertTrue(popups.size() > 0);

        JPopupPanel currPopupPanel = popups.get(popups.size() - 1)
                .getPopupPanel();
        JCommandButton cutButton = getCommandButton(currPopupPanel, "Cut");
        assertNotNull(cutButton);
        JCommandButton pasteButton = getCommandButton(currPopupPanel, "Paste");
        assertNotNull(pasteButton);

        Point cutButtonLoc = cutButton.getLocationOnScreen();
        Rectangle cutPopupArea = cutButton.getUI().getLayoutInfo().popupClickArea;
        Point pasteButtonLoc = pasteButton.getLocationOnScreen();
        Rectangle pastePopupArea = pasteButton.getUI().getLayoutInfo().popupClickArea;

        robot().settings().delayBetweenEvents(500);

        // bring the popup of the cut button
        robot().moveMouse(cutButtonLoc.x + cutPopupArea.x + cutPopupArea.width
                / 2, cutButtonLoc.y + cutPopupArea.y + cutPopupArea.height / 2);
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        Thread.sleep(100);
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        // ribbon should be minimized
        assertTrue(ribbon.isMinimized());
        // and showing in popup
        assertTrue(FlamingoUtilities
                .isShowingMinimizedRibbonInPopup(ribbon));
        // and cut button should show its popup
        assertTrue(cutButton.getPopupModel().isPopupShowing());
        // and paste button should not show its popup
        assertFalse(pasteButton.getPopupModel().isPopupShowing());

        // click in the popup area of paste button
        Thread.sleep(100);
        robot().moveMouse(pasteButtonLoc.x + pastePopupArea.x
                + pastePopupArea.width / 2, pasteButtonLoc.y + pastePopupArea.y
                + pastePopupArea.height / 2);
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        Thread.sleep(100);
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        // ribbon should be minimized
        assertTrue(ribbon.isMinimized());
        // and showing in popup
        assertTrue(FlamingoUtilities
                .isShowingMinimizedRibbonInPopup(ribbon));
        // and cut button should not show its popup
        assertFalse(cutButton.getPopupModel().isPopupShowing());
        // and paste button should show its popup
        assertTrue(pasteButton.getPopupModel().isPopupShowing());

        // click in the popup area once again
        robot().pressMouse(MouseButton.LEFT_BUTTON);
        Thread.sleep(100);
        robot().releaseMouse(MouseButton.LEFT_BUTTON);
        // ribbon should be minimized
        assertTrue(ribbon.isMinimized());
        // and showing in popup
        assertTrue(FlamingoUtilities
                .isShowingMinimizedRibbonInPopup(ribbon));
        // and cut button should not show its popup
        assertFalse(cutButton.getPopupModel().isPopupShowing());
        // and paste button should not show its popup
        assertFalse(pasteButton.getPopupModel().isPopupShowing());
    }

    private static JCommandButton getCommandButton(Component c, String title) {
        if (c instanceof JCommandButton) {
            if (title.equals(((JCommandButton) c).getText()))
                return (JCommandButton) c;
        }

        if (c instanceof Container) {
            Container cont = (Container) c;
            for (int i = 0; i < cont.getComponentCount(); i++) {
                JCommandButton result = getCommandButton(cont.getComponent(i),
                        title);
                if (result != null)
                    return result;
            }
        }

        return null;

    }

}
