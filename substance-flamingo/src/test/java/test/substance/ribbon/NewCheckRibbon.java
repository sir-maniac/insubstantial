/*
 * Copyright (c) 2005-2010 Flamingo / Substance Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Flamingo Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package test.substance.ribbon;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;
import java.util.*;

import javax.swing.*;

import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;
import org.pushingpixels.lafwidget.LafWidgetRepository;
import org.pushingpixels.lafwidget.LafWidgetSupport;
import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceConstants.SubstanceWidgetType;
import org.pushingpixels.substance.api.skin.CeruleanSkin;
import org.pushingpixels.substance.api.skin.OfficeBlack2007Skin;
import org.pushingpixels.substance.flamingo.ribbon.gallery.oob.SubstanceRibbonTask;

import org.pushingpixels.substance.internal.utils.SubstanceColorSchemeUtilities;
import org.pushingpixels.substance.internal.utils.SubstanceImageCreator;
import org.pushingpixels.substance.internal.utils.SubstanceSizeUtils;
import org.pushingpixels.substance.internal.utils.icon.SubstanceIconFactory;
import test.common.IconWrapperResizableIcon;
import test.ribbon.BasicCheckRibbon;

import com.jgoodies.forms.builder.DefaultFormBuilder;

public class NewCheckRibbon extends BasicCheckRibbon {
	private JLabel saveLabel;

    public NewCheckRibbon() {
        super();
        this.setTitle("Ribbon quite a bit longer title to check contextual tabs");
    }



	@Override
	public void configureRibbon() {
		super.configureRibbon();
		this.getRibbon().addTask(SubstanceRibbonTask.getSubstanceRibbonTask());

        LafWidgetSupport lws = LafWidgetRepository.getRepository()
					.getLafSupport();


        final ResizableIcon north = new IconWrapperResizableIcon(lws.getArrowIcon(SwingConstants.NORTH));
        final ResizableIcon south = new IconWrapperResizableIcon(lws.getArrowIcon(SwingConstants.SOUTH));
        final JCommandButton toggleButton = new JCommandButton("", north);
        toggleButton.setDisplayState(CommandButtonDisplayState.SMALL);
        toggleButton.setCommandButtonKind(JCommandButton.CommandButtonKind.ACTION_ONLY);
        toggleButton.getActionModel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean state = !getRibbon().isMinimized();
                getRibbon().setMinimized(state);
                toggleButton.setIcon(state?south:north);
            }
        });


        getRibbon().addHelpPanelComponent(toggleButton);


	}

	@Override
	protected void configureStatusBar() {
		super.configureStatusBar();
		SubstanceLookAndFeel.setDecorationType(this.statusBar,
				DecorationAreaType.FOOTER);
	}

	@Override
	protected void configureControlPanel(DefaultFormBuilder formBuilder) {
		super.configureControlPanel(formBuilder);

		final JCheckBox useThemedDefaultIconsCheckBox = new JCheckBox("use");
		useThemedDefaultIconsCheckBox.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
                    public void run() {
						UIManager
								.put(
										SubstanceLookAndFeel.USE_THEMED_DEFAULT_ICONS,
										useThemedDefaultIconsCheckBox
												.isSelected() ? Boolean.TRUE
												: null);
						SwingUtilities
								.updateComponentTreeUI(NewCheckRibbon.this);
						repaint();
					}
				});
			}
		});
		formBuilder.append("Themed icons", useThemedDefaultIconsCheckBox);

		final JCheckBox heapPanel = new JCheckBox("show");
		heapPanel.setSelected(false);
		heapPanel.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent e) {
				SubstanceLookAndFeel.setWidgetVisible(getRootPane(), heapPanel
						.isSelected(),
						SubstanceWidgetType.TITLE_PANE_HEAP_STATUS);
			}
		});
		formBuilder.append("Heap panel", heapPanel);
	}

	public static void main(String[] args) {
		for (Window w : Window.getWindows()) {
			String wTitle = null;
			JRootPane rootPane = null;
			if (w instanceof Frame) {
				wTitle = ((Frame) w).getTitle();
			}
			if (w instanceof Dialog) {
				wTitle = ((Dialog) w).getTitle();
			}
			if (w instanceof JFrame) {
				rootPane = ((JFrame) w).getRootPane();
			}
			if (w instanceof JDialog) {
				rootPane = ((JDialog) w).getRootPane();
			}
			System.out.println("Window '" + wTitle + "' of "
					+ w.getClass().getName());
			if (rootPane != null) {
				System.out.println("\troot pane UI:"
						+ rootPane.getUI().getClass().getName());
			}
		}

		UIManager.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("lookAndFeel".equals(evt.getPropertyName())) {
					LookAndFeel oldLaf = (LookAndFeel) evt.getOldValue();
					LookAndFeel newLaf = (LookAndFeel) evt.getNewValue();
					System.out.println("Look-and-feel change from "
							+ ((oldLaf == null) ? "null" : oldLaf.getName())
							+ " to "
							+ ((newLaf == null) ? "null" : newLaf.getName()));
				}
			}
		});

		try {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> urls = cl.getResources("META-INF/MANIFEST.MF");
			Set<String> timestampStrings = new HashSet<String>();
			while (urls.hasMoreElements()) {
				InputStream is = urls.nextElement().openStream();
				BufferedReader br = new BufferedReader(
						new InputStreamReader(is));
				while (true) {
					String line = br.readLine();
					if (line == null)
						break;
					int firstColonIndex = line.indexOf(":");
					if (firstColonIndex < 0)
						continue;
					String name = line.substring(0, firstColonIndex).trim();
					if (timestampStrings.contains(name)) {
						continue;
					}
					if (name.endsWith("-BuildStamp")) {
						System.out.println(line);
						timestampStrings.add(name);
					}
				}
				try {
					br.close();
				} catch (IOException ignored) {
				}
			}
			System.out.println();
		} catch (IOException ignored) {
		}

		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
            public void run() {
				SubstanceLookAndFeel.setSkin(new CeruleanSkin());
				NewCheckRibbon c = new NewCheckRibbon();
				c.configureRibbon();
				c.applyComponentOrientation(ComponentOrientation
						.getOrientation(Locale.getDefault()));
				Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getMaximumWindowBounds();
				c.setPreferredSize(new Dimension(r.width, r.height / 2));
				c.setMinimumSize(new Dimension(100, r.height / 3));
				c.pack();
				c.setLocation(r.x, r.y);
				c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				c.setVisible(true);
			}
		});
	}
}
