package utest.ribbon;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import junit.framework.TestCase;

import org.pushingpixels.flamingo.api.common.*;
import org.pushingpixels.flamingo.api.common.icon.EmptyResizableIcon;
import org.pushingpixels.flamingo.api.ribbon.*;
import org.pushingpixels.flamingo.internal.ui.ribbon.JRibbonGallery;

/**
 * Testing the internal {@link JRibbonGallery} class. Each test case operates on
 * the same ribbon gallery that initially has four groups of ten buttons each.
 * 
 * @author Kirill Grouchnikov
 */
public class RibbonGalleryTestCase extends TestCase {
	JCommandToggleButton[][] buttons;

	JRibbonGallery gallery;

	JRibbonFrame ribbonFrame;

	JRibbonBand ribbonBand;

	final static String GALLERY_NAME = "Gallery";

	@Override
	protected void setUp() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() {
            buttons = new JCommandToggleButton[4][10];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 10; j++) {
                    buttons[i][j] = new JCommandToggleButton("Button " + i
                            + ":" + j, new EmptyResizableIcon(32));
                }
            }
    
            ribbonFrame = new JRibbonFrame();
    
            ribbonBand = new JRibbonBand("Band", new EmptyResizableIcon(32));
    
            Map<RibbonElementPriority, Integer> visibleButtonCounts = new HashMap<RibbonElementPriority, Integer>();
            visibleButtonCounts.put(RibbonElementPriority.LOW, 1);
            visibleButtonCounts.put(RibbonElementPriority.MEDIUM, 6);
            visibleButtonCounts.put(RibbonElementPriority.TOP, 10);
    
            List<StringValuePair<List<JCommandToggleButton>>> galleryButtons = new ArrayList<StringValuePair<List<JCommandToggleButton>>>();
            for (int i = 0; i < 4; i++) {
                List<JCommandToggleButton> galleryButtonsList = new ArrayList<JCommandToggleButton>();
                for (int j = 0; j < 10; j++) {
                    galleryButtonsList.add(buttons[i][j]);
                }
                galleryButtons.add(new StringValuePair<List<JCommandToggleButton>>(
                        "Group " + i, galleryButtonsList));
            }
    
            ribbonBand.addRibbonGallery(GALLERY_NAME, galleryButtons,
                    visibleButtonCounts, 6, 4, RibbonElementPriority.TOP);
            ribbonFrame.getRibbon().addTask(
                    new RibbonTask("Task 0", ribbonBand));
    
            gallery = ribbonBand.getControlPanel().getRibbonGallery(
                    GALLERY_NAME);
        }});
	}

	public void testCreation() {
		assertEquals(gallery.getButtonGroupCount(), 4);
		assertEquals(gallery.getButtonCount(), 40);
		for (int i = 0; i < gallery.getButtonGroupCount(); i++) {
			List<JCommandToggleButton> buttonGroup = gallery
					.getButtonGroup("Group " + i);
			assertEquals(buttonGroup.size(), 10);
			for (int j = 0; j < 10; j++) {
				assertEquals(buttonGroup.get(j), buttons[i][j]);
			}
		}
	}

	public void testSelection() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() {
            for (int i = 0; i < gallery.getButtonCount(); i++) {
                assertFalse(gallery.getButtonAt(i).getActionModel()
                        .isSelected());
            }
            int[][] toTest = { { 0, 0 }, { 1, 5 }, { 2, 1 }, { 0, 8 }, { 3, 6 } };
            for (int sel = 0; sel < toTest.length; sel++) {
                int selRow = toTest[sel][0];
                int selCol = toTest[sel][1];
                ribbonBand.setSelectedRibbonGalleryButton(GALLERY_NAME,
                        buttons[selRow][selCol]);
                for (int i = 0; i < gallery.getButtonGroupCount(); i++) {
                    List<JCommandToggleButton> buttonGroup = gallery
                            .getButtonGroup("Group " + i);
                    for (int j = 0; j < 10; j++) {
                        JCommandToggleButton button = buttonGroup.get(j);
                        if ((i == selRow) && (j == selCol))
                            assertTrue(button.getActionModel().isSelected());
                        else
                            assertFalse(button.getActionModel().isSelected());
                    }
                }
            }
        }});
	}

	public void testSelectionWithMouse() {
		final CountDownLatch latch = new CountDownLatch(1);
		final Throwable[] ts = new Throwable[1];
		for (int i = 0; i < gallery.getButtonCount(); i++) {
			assertFalse(gallery.getButtonAt(i).getActionModel()
					.isSelected());
		}

		final int[] toTest = { 0, 1, 2, 3, 4, 5, 6 };
		final boolean[] tested = new boolean[toTest.length];

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getMaximumWindowBounds();
				ribbonFrame.setPreferredSize(new Dimension(r.width,
						r.height / 2));
				ribbonFrame.pack();
				ribbonFrame.setLocation(r.x, r.y);
				ribbonFrame.setVisible(true);
				ribbonFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

				Thread.currentThread().setUncaughtExceptionHandler(
						new Thread.UncaughtExceptionHandler() {
							@Override
							public void uncaughtException(Thread t, Throwable e) {
								ts[0] = e;
								e.printStackTrace();
							}
						});

				new Thread() {
					@Override
					public void run() {
						try {
							Robot robot = new Robot();

							for (int sel = 0; sel < toTest.length; sel++) {
								int selCol = toTest[sel];
								final JCommandToggleButton toClick = buttons[0][selCol];
								Point buttonLeftTop = toClick
										.getLocationOnScreen();
								int buttonWidth = toClick.getWidth();
								int buttonHeight = toClick.getHeight();
								robot
										.mouseMove(buttonLeftTop.x
												+ buttonWidth / 2,
												buttonLeftTop.y + buttonHeight
														/ 2);
								robot.mousePress(InputEvent.BUTTON1_MASK);
								robot.mouseRelease(InputEvent.BUTTON1_MASK);
								Thread.sleep(1000);

								final int fSel = sel;
								SwingUtilities.invokeAndWait(new Runnable() {
									@Override
                                    public void run() {
										// System.out.println(toClick.getText()
										// + " selection is "
										// + toClick.getActionModel()
										// .isSelected());

										int selectedCount = 0;
										for (int i = 0; i < gallery
												.getButtonGroupCount(); i++) {
											List<JCommandToggleButton> buttonGroup = gallery
													.getButtonGroup("Group "
															+ i);
											for (int j = 0; j < buttonGroup
													.size(); j++) {
												JCommandToggleButton button = buttonGroup
														.get(j);
												if (button.getActionModel()
														.isSelected())
													selectedCount++;
											}
										}

										tested[fSel] = (selectedCount == 1)
												&& gallery.getButtonGroup(
														"Group 0").get(fSel)
														.getActionModel()
														.isSelected();
									};
								});
							}
						} catch (Throwable t) {
							ts[0] = t;
						}
						new Thread() {
							@Override
							public void run() {
								ribbonFrame.dispose();
								latch.countDown();
							}
						}.start();
					}
				}.start();
			}
		});
		try {
			assertTrue(latch.await(10, TimeUnit.SECONDS));
		} catch (Exception exc) {
		}
		assertNull(ts[0]);
		for (boolean test : tested)
			assertTrue(test);
	}

	public void testRemoval() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() {
            ribbonBand.removeRibbonGalleryButtons(GALLERY_NAME,
                    buttons[0][0]);
            assertEquals(gallery.getButtonGroupCount(), 4);
            assertEquals(gallery.getButtonCount(), 39);
            for (int i = 0; i < gallery.getButtonGroupCount(); i++) {
                List<JCommandToggleButton> buttonGroup = gallery
                        .getButtonGroup("Group " + i);
                if (i == 0) {
                    assertEquals(buttonGroup.size(), 9);
                    for (int j = 0; j < 9; j++) {
                        assertEquals(buttonGroup.get(j), buttons[i][j + 1]);
                    }
                } else {
                    assertEquals(buttonGroup.size(), 10);
                    for (int j = 0; j < 10; j++) {
                        assertEquals(buttonGroup.get(j), buttons[i][j]);
                    }
                }
            }
        }});
	}

	public void testRemoval2() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() {
            ribbonBand.removeRibbonGalleryButtons(GALLERY_NAME,
                    buttons[1][5]);
            assertEquals(gallery.getButtonGroupCount(), 4);
            assertEquals(gallery.getButtonCount(), 39);
            for (int i = 0; i < gallery.getButtonGroupCount(); i++) {
                List<JCommandToggleButton> buttonGroup = gallery
                        .getButtonGroup("Group " + i);
                if (i == 1) {
                    assertEquals(buttonGroup.size(), 9);
                    for (int j = 0; j < 5; j++) {
                        assertEquals(buttonGroup.get(j), buttons[i][j]);
                    }
                    for (int j = 5; j < 9; j++) {
                        assertEquals(buttonGroup.get(j), buttons[i][j + 1]);
                    }
                } else {
                    assertEquals(buttonGroup.size(), 10);
                    for (int j = 0; j < 10; j++) {
                        assertEquals(buttonGroup.get(j), buttons[i][j]);
                    }
                }
            }
        }});
	}

	public void testRemoval3() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() {
            ribbonBand.removeRibbonGalleryButtons(GALLERY_NAME,
                    buttons[2][9], buttons[1][9]);
            assertEquals(gallery.getButtonGroupCount(), 4);
            assertEquals(gallery.getButtonCount(), 38);
            for (int i = 0; i < gallery.getButtonGroupCount(); i++) {
                List<JCommandToggleButton> buttonGroup = gallery
                        .getButtonGroup("Group " + i);
                if ((i == 2) || (i == 1)) {
                    assertEquals(buttonGroup.size(), 9);
                    for (int j = 0; j < 9; j++) {
                        assertEquals(buttonGroup.get(j), buttons[i][j]);
                    }
                } else {
                    assertEquals(buttonGroup.size(), 10);
                    for (int j = 0; j < 10; j++) {
                        assertEquals(buttonGroup.get(j), buttons[i][j]);
                    }
                }
            }
        }});
	}

	public void testRemovalAll() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() {
            int removed = 0;
            for (int i = 0; i < 4; i++) {
                List<Integer> indexes = new ArrayList<Integer>();
                for (int j = 0; j < 10; j++) {
                    indexes.add(j);
                }
                Collections.shuffle(indexes);
                for (int toRemove : indexes) {
                    ribbonBand.removeRibbonGalleryButtons(GALLERY_NAME,
                            buttons[i][toRemove]);
                    removed++;
                    assertEquals(gallery.getButtonCount(), 40 - removed);
                }
            }
            assertEquals(gallery.getButtonCount(), 0);
        }});
	}

	public void testRemovalAll2() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() {
            int removed = 0;
            for (int i = 0; i < 4; i++) {
                List<Integer> indexes = new ArrayList<Integer>();
                for (int j = 0; j < 5; j++) {
                    indexes.add(j);
                }
                Collections.shuffle(indexes);
                for (int toRemove : indexes) {
                    ribbonBand.removeRibbonGalleryButtons(GALLERY_NAME,
                            buttons[i][toRemove], buttons[i][9 - toRemove]);
                    removed += 2;
                    assertEquals(gallery.getButtonCount(), 40 - removed);
                }
            }
            assertEquals(gallery.getButtonCount(), 0);
        }});
	}

	public void testRemovalAll3() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() {
            int removed = 0;
            for (int i = 0; i < 2; i++) {
                List<Integer> indexes = new ArrayList<Integer>();
                for (int j = 0; j < 5; j++) {
                    indexes.add(j);
                }
                Collections.shuffle(indexes);
                for (int toRemove : indexes) {
                    ribbonBand.removeRibbonGalleryButtons(GALLERY_NAME,
                            buttons[i][toRemove], buttons[i][9 - toRemove],
                            buttons[i + 2][toRemove], buttons[i + 2][9 - toRemove]);
                    removed += 4;
                    assertEquals(gallery.getButtonCount(), 40 - removed);
                }
            }
            assertEquals(gallery.getButtonCount(), 0);
        }});
	}

	public void testRemovalAndSelection() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() {
            for (int i = 0; i < gallery.getButtonCount(); i++) {
                assertFalse(gallery.getButtonAt(i).getActionModel()
                        .isSelected());
            }

            int[][] toTest = { { 0, 2 }, { 1, 6 }, { 2, 8 }, { 0, 3 }, { 3, 1 } };
            for (int sel = 0; sel < toTest.length; sel++) {
                int selRow = toTest[sel][0];
                int selCol = toTest[sel][1];
                ribbonBand.setSelectedRibbonGalleryButton(GALLERY_NAME,
                        buttons[selRow][selCol]);
                ribbonBand.removeRibbonGalleryButtons(GALLERY_NAME,
                        buttons[selRow][selCol]);
                for (int i = 0; i < gallery.getButtonGroupCount(); i++) {
                    List<JCommandToggleButton> buttonGroup = gallery
                            .getButtonGroup("Group " + i);
                    for (int j = 0; j < buttonGroup.size(); j++) {
                        JCommandToggleButton button = buttonGroup.get(j);
                        assertFalse(button.getActionModel().isSelected());
                    }
                }
            }
        }});
	}

	public void testAddition() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() { @Override public void run() {
            JCommandToggleButton newButton = new JCommandToggleButton("New",
                    new EmptyResizableIcon(32));
            ribbonBand.addRibbonGalleryButtons(GALLERY_NAME, "Group 0",
                    newButton);

            assertEquals(gallery.getButtonGroupCount(), 4);
            assertEquals(gallery.getButtonCount(), 41);
            assertEquals(gallery.getButtonGroup("Group 0").size(), 11);
            assertEquals(gallery.getButtonGroup("Group 1").size(), 10);
            assertEquals(gallery.getButtonGroup("Group 2").size(), 10);
            assertEquals(gallery.getButtonGroup("Group 3").size(), 10);

            JCommandToggleButton newButton2 = new JCommandToggleButton("New 2",
                    new EmptyResizableIcon(32));
            ribbonBand.addRibbonGalleryButtons(GALLERY_NAME,
                    "Group non-existent", newButton2);

            assertEquals(gallery.getButtonGroupCount(), 4);
            assertEquals(gallery.getButtonCount(), 41);
            assertEquals(gallery.getButtonGroup("Group 0").size(), 11);
            assertEquals(gallery.getButtonGroup("Group 1").size(), 10);
            assertEquals(gallery.getButtonGroup("Group 2").size(), 10);
            assertEquals(gallery.getButtonGroup("Group 3").size(), 10);
            assertTrue(gallery.getButtonGroup("Group 0").contains(newButton));

            JCommandToggleButton newButton3 = new JCommandToggleButton("New 3",
                    new EmptyResizableIcon(32));
            JCommandToggleButton newButton4 = new JCommandToggleButton("New 4",
                    new EmptyResizableIcon(32));
            ribbonBand.addRibbonGalleryButtons(GALLERY_NAME, "Group 2",
                    newButton3, newButton4);

            assertEquals(gallery.getButtonGroupCount(), 4);
            assertEquals(gallery.getButtonCount(), 43);
            assertEquals(gallery.getButtonGroup("Group 0").size(), 11);
            assertEquals(gallery.getButtonGroup("Group 1").size(), 10);
            assertEquals(gallery.getButtonGroup("Group 2").size(), 12);
            assertEquals(gallery.getButtonGroup("Group 3").size(), 10);
            assertTrue(gallery.getButtonGroup("Group 0").contains(newButton));
            assertTrue(gallery.getButtonGroup("Group 2").contains(newButton3));
            assertTrue(gallery.getButtonGroup("Group 2").contains(newButton4));
        }});
	}
}
