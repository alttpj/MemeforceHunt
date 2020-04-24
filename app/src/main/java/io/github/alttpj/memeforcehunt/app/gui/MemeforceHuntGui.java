/*
 * Copyright 2020-2020 the ALttPJ Team @ https://github.com/alttpj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.alttpj.memeforcehunt.app.gui;


import io.github.alttpj.memeforcehunt.common.sprites.DefaultSpritemapWithSkins;
import io.github.alttpj.memeforcehunt.common.value.SpritemapWithSkin;
import io.github.alttpj.memeforcehunt.lib.AlttpRomPatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class MemeforceHuntGui implements Callable<Integer> {

  private static final Logger LOG = LoggerFactory.getLogger(MemeforceHuntGui.class);

  private static final URI LINK = URI.create("https://github.com/alttpj/MemeforceHunt/releases");

  private static final int PER_ROW = 8;

  private static final List<SpritemapWithSkin> SPRITEMAPS_WITH_SKIN = DefaultSpritemapWithSkins.values();

  private JTextField fileName;
  private JLabel preview;
  private JLabel skinsText;
  private JComboBox<SpritemapWithSkin> skins;

  @Override
  public Integer call() throws Exception {
    javax.swing.SwingUtilities.invokeLater(() -> {
      try {
        printGUI();
      } catch (final Exception runEx) {
        LOG.error("Problem encountered starting or running the GUI.", runEx);
      }
    });

    return 0;
  }

  public void printGUI() {
    // try to set LaF
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (final Exception lookAndFeelEx) {
      LOG.warn("Unable to set system L&F.", lookAndFeelEx);
      // do nothing
    } //end System

    final BorderLayout borderLayout = new BorderLayout();
    borderLayout.setHgap(5);
    borderLayout.setVgap(5);
    final JFrame frame = new JFrame();
    frame.setTitle("Memeforce Hunt v" + AlttpRomPatcher.getVersion());
    frame.setLayout(borderLayout);

    /* *************************************
     * left side
     ***************************************/
    final JPanel topBar = createTopBar();
    frame.add(topBar, BorderLayout.NORTH);


    /* *************************************
     * right side
     ***************************************/
    final JPanel rightColumn = createRightColumn(frame);
    frame.add(rightColumn, BorderLayout.EAST);


    /* *************************************
     * center / main / icon chooser
     ***************************************/
    final JPanel iconSelector = createIconSelector();
    frame.add(iconSelector, BorderLayout.CENTER);

    /* *************************************
     * bottom for baseline
     ***************************************/
    final JPanel base = new JPanel();
    frame.add(base, BorderLayout.SOUTH);


    /* *************************************
     * window stuff
     ***************************************/
    // ico
    BufferedImage ico;
    try {
      ico = ImageIO.read(DefaultSpritemapWithSkins.class.getResourceAsStream("/triforce piece.png"));
    } catch (final IOException imageReadEx) {
      LOG.warn("Unable to set app window icon.", imageReadEx);
      ico = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
    }

    frame.setIconImage(ico);

    // frame setting
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationByPlatform(true);
    frame.setResizable(true);
    frame.pack();
    frame.setSize(640, 544);

    frame.setVisible(true);
  }

  /**
   * Create the main icon selector.
   *
   * <p>It is just a little odd how this works.<br>
   * First there is a hidden combo box. It is hidden, but used for the internal selection
   * which item is currently selected. Kind of a 'state holder'.<br>
   * It is also used as a indexed list for the random button.</p>
   */
  private JPanel createIconSelector() {
    final JPanel iconGrid = new JPanel(new GridBagLayout());
    iconGrid.setBorder(SwingAppConstants.PADDING);
    final GridBagConstraints iconListGridConstraints = new GridBagConstraints();

    iconListGridConstraints.fill = GridBagConstraints.HORIZONTAL;
    iconListGridConstraints.gridy = 0;
    iconListGridConstraints.gridx = 0;

    /* *************************************
     * model (data only)
     ***************************************/
    this.skins = new JComboBox<>(SPRITEMAPS_WITH_SKIN.toArray(new SpritemapWithSkin[0]));
    this.skins.setEditable(false);

    for (final SpritemapWithSkin spritemapWithSkin : SPRITEMAPS_WITH_SKIN) {
      final SkinButton sb = new SkinButton(spritemapWithSkin);
      sb.addActionListener(actionEvent -> this.skins.setSelectedItem(sb.getSpritemapWithSkin()));
      iconGrid.add(sb, iconListGridConstraints);
      sb.setToolTipText("Use Skin \"" + spritemapWithSkin.getDescription() + "\".");
      iconListGridConstraints.gridx++;
      if (iconListGridConstraints.gridx == PER_ROW) {
        iconListGridConstraints.gridx = 0;
        iconListGridConstraints.gridy++;
      }
    }

    this.skins.addItemListener(
        (ItemEvent itemEvent) -> {
          final SpritemapWithSkin sel = (SpritemapWithSkin) this.skins.getSelectedItem();
          this.preview.setIcon(sel.getImageIcon());
          this.skinsText.setText(sel.getDescription());
        });

    return iconGrid;
  }

  private JPanel createRightColumn(final JFrame parent) {
    final GridBagLayout gridBagLayout = new GridBagLayout();
    final JPanel rightColumn = new JPanel(gridBagLayout);
    rightColumn.setBorder(SwingAppConstants.PADDING);

    final GridBagConstraints gbc = createGridBagConstraints(gridBagLayout);

    // skin text
    this.skinsText = new JLabel("---");
    this.skinsText.setHorizontalAlignment(SwingConstants.CENTER);
    this.skinsText.setText(DefaultSpritemapWithSkins.getByName("benana").getDescription());
    rightColumn.add(this.skinsText, gbc);
    gbc.gridy++;

    // preview
    this.preview = new JLabel();
    this.preview.setHorizontalAlignment(SwingConstants.CENTER);
    this.preview.setIcon(DefaultSpritemapWithSkins.getByName("benana").getImageIcon());
    rightColumn.add(this.preview, gbc);
    gbc.gridy++;

    // spacer
    rightColumn.add(new JLabel(" "), gbc);
    gbc.gridy++;

    // patch button
    final JButton doPatch = createPatchButton(parent, gbc);
    rightColumn.add(doPatch, gbc);

    // random patch button
    final JButton doRandom = createRandomPatchButton(parent, gbc);
    rightColumn.add(doRandom, gbc);

    rightColumn.add(new JLabel(""), gbc);
    gbc.gridy++;
    rightColumn.add(new JLabel(""), gbc);
    gbc.gridy++;
    rightColumn.add(new JLabel(""), gbc);
    gbc.gridy++;

    // update checks
    final JButton update = createUpdateCheckButton(parent);
    rightColumn.add(update, gbc);

    return rightColumn;
  }

  private GridBagConstraints createGridBagConstraints(final GridBagLayout gridBagLayout) {
    final GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.ipadx = 10;
    gbc.ipady = 10;
    gbc.anchor = GridBagConstraints.NORTH;
    gridBagLayout.rowWeights = new double[] {
        // skintext, preview
        0.0, 0.0,
        // spacer 1
        0.2,
        // patch, random
        0.0, 0.0,
        // spacer 2+3
        1.0, 1.0, 1.0,
        // update
        0.0
    };
    gbc.weighty = 0.5;
    return gbc;
  }

  private JButton createUpdateCheckButton(final JFrame parent) {
    final JButton update = new JButton("Check for updates");

    update.addActionListener(
        (ActionEvent actionEvent) -> {
          try {
            final Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
              desktop.browse(LINK);
            }
          } catch (final Exception urlOpenEx) {
            JOptionPane.showMessageDialog(parent,
                "uhhh",
                "Houston, we have a problem.",
                JOptionPane.WARNING_MESSAGE);
            LOG.error("Problem opening the URI [{}].", LINK, urlOpenEx);
          }
        });
    return update;
  }

  private JButton createRandomPatchButton(final JFrame parent, final GridBagConstraints gbc) {
    final JButton doRandom = new JButton("Surprise me");
    gbc.gridy++;

    doRandom.addActionListener(
        actionEvent -> {
          final String fileNameText = this.fileName.getText();
          final int r = (int) (Math.random() * SPRITEMAPS_WITH_SKIN.size());

          try {
            new AlttpRomPatcher().patchROM(fileNameText, SPRITEMAPS_WITH_SKIN.get(r));
          } catch (final Exception patchException) {
            JOptionPane.showMessageDialog(parent,
                "Something went wrong.\n\n"
                    + "Error Message:\n"
                    + "" + patchException.getMessage(),
                "PROBLEM",
                JOptionPane.WARNING_MESSAGE,
                DefaultSpritemapWithSkins.getByName("scream").getImageIcon());
            return;
          }
          JOptionPane.showMessageDialog(parent,
              "SUCCESS",
              "Enjoy",
              JOptionPane.PLAIN_MESSAGE,
              DefaultSpritemapWithSkins.getByName("benana").getImageIcon());
        });

    return doRandom;
  }

  private JButton createPatchButton(final JFrame parent, final GridBagConstraints gbc) {
    final JButton doPatch = new JButton("Patch");
    gbc.gridy++;

    doPatch.addActionListener(
        actionEvent -> {
          final String fileNameText = this.fileName.getText();
          final SpritemapWithSkin selectedItem;

          try {
            selectedItem = (SpritemapWithSkin) this.skins.getSelectedItem();
            new AlttpRomPatcher().patchROM(fileNameText, selectedItem);
          } catch (final Exception patchEx) {
            LOG.error("Error patching.", patchEx);
            JOptionPane.showMessageDialog(parent,
                "Something went wrong: [" + patchEx.getMessage() + "].",
                "PROBLEM",
                JOptionPane.WARNING_MESSAGE,
                DefaultSpritemapWithSkins.getByName("scream").getImageIcon());
            return;
          }

          JOptionPane.showMessageDialog(parent,
              "SUCCESS",
              "Enjoy",
              JOptionPane.PLAIN_MESSAGE,
              Optional.ofNullable(selectedItem)
                  .map(SpritemapWithSkin::getImageIcon)
                  .orElseGet(() -> DefaultSpritemapWithSkins.getByName("benana").getImageIcon()));
        });
    return doPatch;
  }

  private JPanel createTopBar() {
    final JPanel topBar = new JPanel();
    topBar.setBorder(SwingAppConstants.PADDING);
    topBar.setLayout(new BoxLayout(topBar, BoxLayout.LINE_AXIS));

    // rom name
    this.fileName = new JTextField("");
    topBar.add(this.fileName);

    topBar.add(Box.createRigidArea(new Dimension(10, 0)));

    // file chooser
    final JButton find = new JButton("Open…");
    topBar.add(find);

    // file explorer
    final FileDialog explorer = new java.awt.FileDialog((java.awt.Frame) null);
    final FilenameFilter filenameFilter = (dir, name) -> name.endsWith("sfc");
    explorer.setFilenameFilter(filenameFilter);

    // load sprite file
    find.addActionListener(
        actionEvent -> {
          //explorer.setSelectedFile(EEE);
          explorer.setMode(FileDialog.LOAD);
          explorer.setVisible(true);

          if (explorer.getFile() == null) {
            return;
          }

          // read the file
          final File selectedFile = new File(explorer.getDirectory(), explorer.getFile());

          final String absolutePath = selectedFile.getAbsolutePath();
          if (absolutePath.endsWith(".sfc")) {
            this.fileName.setText(absolutePath);
          }
        });

    return topBar;
  }
}
