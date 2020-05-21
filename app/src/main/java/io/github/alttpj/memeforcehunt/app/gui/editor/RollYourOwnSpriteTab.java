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

package io.github.alttpj.memeforcehunt.app.gui.editor;

import io.github.alttpj.memeforcehunt.app.gui.actions.StaticGuiActions;
import io.github.alttpj.memeforcehunt.app.gui.properties.SelectedFileProperty;

import io.github.alttpj.library.image.SnesTilePacker;
import io.github.alttpj.library.image.Tile;
import io.github.alttpj.library.image.TiledSprite;
import io.github.alttpj.library.image.palette.Palette;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RollYourOwnSpriteTab extends HBox implements Initializable {

  private static final java.util.logging.Logger LOG = Logger.getLogger(RollYourOwnSpriteTab.class.getCanonicalName());

  @FXML
  private SpriteGridCanvas spriteGridCanvas;

  @FXML
  private SpritePreview previewItem;

  @FXML
  private Rectangle previewChest;

  @FXML
  private ColorSelector colorSelector;

  @FXML
  private Button exportButton;

  @FXML
  private PaletteSelector paletteSelector;

  @FXML
  private Button patchButton;

  private final SelectedFileProperty selectedFile = new SelectedFileProperty();

  public RollYourOwnSpriteTab() {
    // fmxl
    final FXMLLoader fxmlLoader =
        new FXMLLoader(getClass().getResource("/io/github/alttpj/memeforcehunt/app/gui/editor/RollYourOwnSpriteTab.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {
      fxmlLoader.load();
    } catch (final IOException ioException) {
      throw new RuntimeException(ioException);
    }
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    this.previewChest.heightProperty().bind(this.previewChest.widthProperty());

    this.spriteGridCanvas.selectedColorSelectorCellProperty().bind(this.colorSelector.selectedColorProperty());
    this.paletteSelector.selectedPaletteProperty().addListener((source, old, newPalette) -> {
      this.colorSelector.setColors(newPalette);
      this.spriteGridCanvas.getPaintableGrid().paletteSwap(newPalette);
    });

    bindPreview();
    bindSelectedFile();
  }

  private void bindSelectedFile() {
    this.patchButton.setDisable(true);
    this.selectedFile.addListener(
        (ObservableValue<?> observableValue, Object oldVal, Object newVal) -> {
          final Optional<File> selectedFile = (Optional<File>) newVal;

          if (selectedFile.isEmpty()) {
            this.patchButton.setDisable(true);
            return;
          }

          this.patchButton.setDisable(false);
        });
  }

  private void bindPreview() {
    this.previewItem.getPaintableGrid().getCells()
        .forEach(cell -> cell.paintedProperty()
            .bind(this.spriteGridCanvas.getPaintableGrid().getCells().get(cell.getIndex()).paintedProperty()));
  }

  @FXML
  public void doPatchRom(final ActionEvent actionEvent) {
    final Palette selectedPalette = this.paletteSelector.getSelectedPalette();
    final byte[][] tileBytes = packTilesArray();
    final Tile[] tiles = Arrays.stream(tileBytes)
        .map(tileByte -> (Tile) () -> tileByte)
        .toArray(Tile[]::new);

    final TiledSprite tiledSprite = new TiledSprite() {
      @Override
      public Tile[] getTiles() {
        return tiles;
      }

      @Override
      public Palette getPalette() {
        return selectedPalette;
      }
    };
    try {
      StaticGuiActions.patch(this.selectedFile.get().orElseThrow(), tiledSprite);
      final Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Rom file patched successfully.");
      alert.setHeaderText("Custom sprite");
      alert.setContentText("The custom sprite was successfully patched into your rom.");

      alert.showAndWait();

    } catch (final IOException ioException) {
      LOG.log(Level.SEVERE, ioException,
          () -> "Error patching custom sprite into [" + this.selectedFile.get() + "].");
      final Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Unable to patch rom");
      alert.setHeaderText("Error patching supplied rom file.");
      alert.setContentText("Error message was: " + ioException.getMessage() + "\n"
          + "If the error persists, please start MemeforceHunt from the command line "
          + "and report the StackTrace on https://github.com/alttpj/MemeforceHunt/issues/.");

      alert.showAndWait();
    }
  }


  @FXML
  public void onDebug(final ActionEvent actionEvent) {
    LOG.log(Level.INFO, "THIS: " + this);
    LOG.log(Level.INFO, "sprite canvas: " + this.spriteGridCanvas);
    LOG.log(Level.INFO, "sprite canvas bgim: " + this.spriteGridCanvas.getBgImage());
    LOG.log(Level.INFO, "sprite canvas grid: " + this.spriteGridCanvas.getPaintableGrid());

    this.spriteGridCanvas.getPaintableGrid().getCells()
        .forEach(cell -> LOG.log(Level.INFO, "Cell: [" + cell + "]."));
  }

  @FXML
  public void onImport(final ActionEvent actionEvent) {
    // TODO: Import (waiting for #23)
  }

  @FXML
  public void onExport(final ActionEvent actionEvent) {
    packTiles();
    // TODO: Save (waiting for #23)
  }

  public Optional<File> getSelectedFile() {
    return this.selectedFile.get();
  }

  public SelectedFileProperty selectedFileProperty() {
    return this.selectedFile;
  }

  private byte[] packTiles() {
    final byte[] unpackedTiles = getTileBytes();

    return new SnesTilePacker().pack3bppTiles(unpackedTiles);
  }

  private byte[][] packTilesArray() {
    final byte[] unpackedTiles = getTileBytes();

    return new SnesTilePacker().pack3bppTilesIntoTiles(unpackedTiles);
  }

  private byte[] getTileBytes() {
    final List<ColourableCell> cells = this.spriteGridCanvas.getPaintableGrid().getCells();
    final Byte[] unpackedBytes1616 = cells.stream().map(ColourableCell::getSnesPaletteIndex).toArray(Byte[]::new);
    // the bytes are arranged in a 16x16 grid. Instead, we need 4 8x8 grids.
    final byte[] unpackedTiles = new byte[unpackedBytes1616.length];

    for (int index = 0; index < unpackedBytes1616.length; index++) {
      final int inputRownum = index / 16;
      final int indexInTile = (index % 8 + 8 * inputRownum) % 64;

      if (index < unpackedBytes1616.length / 2 && (index / 8) % 2 == 0) {
        // 1st  tile
        unpackedTiles[indexInTile] = unpackedBytes1616[index];
      }

      if (index < unpackedBytes1616.length / 2 && (index / 8) % 2 == 1) {
        // 2nd  tile
        unpackedTiles[indexInTile + 64] = unpackedBytes1616[index];
      }

      if (index >= unpackedBytes1616.length / 2 && (index / 8) % 2 == 0) {
        // 3rd  tile
        unpackedTiles[indexInTile + 128] = unpackedBytes1616[index];
      }

      if (index >= unpackedBytes1616.length / 2 && (index / 8) % 2 == 1) {
        // 3rd  tile
        unpackedTiles[indexInTile + 192] = unpackedBytes1616[index];
      }
    }
    return unpackedTiles;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "RollYourOwnSpriteTab{", "}")
        .add("spriteGridCanvas=" + this.spriteGridCanvas)
        .add("previewItem=" + this.previewItem)
        .add("previewChest=" + this.previewChest)
        .toString();
  }
}
