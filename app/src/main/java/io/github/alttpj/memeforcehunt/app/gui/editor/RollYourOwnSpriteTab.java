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
import io.github.alttpj.memeforcehunt.common.value.ULID;
import io.github.alttpj.memeforcehunt.lib.SpriteFileFormat;
import io.github.alttpj.memeforcehunt.lib.SpriteFileFormatFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.alttpj.library.image.SnesTilePacker;
import io.github.alttpj.library.image.Tile;
import io.github.alttpj.library.image.TiledSprite;
import io.github.alttpj.library.image.palette.Palette;
import io.github.alttpj.library.image.palette.Palette3bpp;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RollYourOwnSpriteTab extends HBox implements Initializable {

  private static final Logger LOG = Logger.getLogger(RollYourOwnSpriteTab.class.getCanonicalName());

  private static final FileChooser.ExtensionFilter SPRYAML_EXTENSION_FILTER = new FileChooser.ExtensionFilter(
      "ZSPR YAML sprite files (*.zspr.yaml)", "*.zspr.yaml");

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

  // metadata for saving -- may be shown at a later time.

  /**
   * Display name for export.
   */
  private final StringProperty displayNameProperty = new SimpleStringProperty("");

  /**
   * Author name for export.
   */
  private final StringProperty authorNameProperty = new SimpleStringProperty("");

  /**
   * Description for the file.
   */
  private final StringProperty descriptionProperty = new SimpleStringProperty("");

  private final ListProperty<String> tags = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

  private final StringProperty ulid = new SimpleStringProperty();

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
    // show file dialog
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Sprite File");
    fileChooser.getExtensionFilters().add(SPRYAML_EXTENSION_FILTER);
    final File file = fileChooser.showOpenDialog(this.getScene().getWindow());

    if (file == null) {
      return;
    }

    try {
      applyFile(file);
    } catch (final IOException ioException) {
      final Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Sprite save error");
      alert.setHeaderText("An exception occured while saving your sprite to a file.");
      alert.setContentText("The exception message is: [" + ioException.getMessage() + "].\n"
          + "If you encounter this problem frequently or think this shouldn't have happened, please "
          + "look for known bugs at https://github.com/alttpj/MemeforceHunt/issues or restart MemeforceHunt "
          + "via the command line and check the console output.");
      alert.initModality(Modality.WINDOW_MODAL);
      alert.showAndWait();
    }

  }

  private void applyFile(final File file) throws IOException {
    final SpriteFileFormat spriteFileFormat = SpriteFileFormatFactory.fromFile(file);

    final PaintableGrid paintableGrid = this.spriteGridCanvas.getPaintableGrid();
    switch (spriteFileFormat.colorPaletteName()) {
      case "RED":
        this.paletteSelector.select(Palette3bpp.RED);
        this.colorSelector.setColors(Palette3bpp.RED);
        paintableGrid.paletteSwap(Palette3bpp.RED);
        break;
      case "BLUE":
        this.paletteSelector.select(Palette3bpp.BLUE);
        this.colorSelector.setColors(Palette3bpp.BLUE);
        paintableGrid.paletteSwap(Palette3bpp.BLUE);
        break;
      case "GREEN":
      default:
        this.paletteSelector.select(Palette3bpp.GREEN);
        this.colorSelector.setColors(Palette3bpp.GREEN);
        paintableGrid.paletteSwap(Palette3bpp.GREEN);
    }

    final Tile[] tiles = new Tile[] {
        () -> Arrays.copyOfRange(spriteFileFormat.data(), 0, 24),
        () -> Arrays.copyOfRange(spriteFileFormat.data(), 24, 48),
        () -> Arrays.copyOfRange(spriteFileFormat.data(), 48, 72),
        () -> Arrays.copyOfRange(spriteFileFormat.data(), 72, 96)
    };

    new Painter().paint(paintableGrid, tiles, this.colorSelector);

    this.displayNameProperty.set(spriteFileFormat.displayName());
    this.authorNameProperty.set(spriteFileFormat.authorName());
    this.descriptionProperty.set(spriteFileFormat.description().orElse(""));
    this.ulid.set(spriteFileFormat.ulid().toString());
    this.tags.clear();
    this.tags.addAll(spriteFileFormat.tags());
  }

  @FXML
  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "patching user supplied ROM file"
  )
  public void onExport(final ActionEvent actionEvent) {
    final byte[] bytes = packTiles();


    // show file dialog
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Sprite File");
    fileChooser.getExtensionFilters().add(SPRYAML_EXTENSION_FILTER);
    File file = fileChooser.showSaveDialog(this.getScene().getWindow());

    if (file == null) {
      return;
    }

    if (!file.getName().endsWith(".zspr.yaml")) {
      file = new File(file.getAbsolutePath() + ".zspr.yaml");
    }

    if (!queryMetadata()) {
      return;
    }

    // convert
    // retain ULID if not null.
    final ULID.Value ulid = Optional.ofNullable(this.ulid.get())
        .map(ULID::parseULID)
        .orElseGet(() -> new ULID().nextValue());
    final SpriteFileFormat spriteFileFormat = SpriteFileFormatFactory.create(
        ulid,
        this.displayNameProperty.get(),
        this.authorNameProperty.get(),
        bytes,
        this.paletteSelector.getSelectedPalette(),
        this.descriptionProperty.get(),
        this.tags.get()
    );

    doSaveFile(file, spriteFileFormat);
  }

  private void doSaveFile(final File file, final SpriteFileFormat spriteFileFormat) {
    try {
      SpriteFileFormatFactory.saveFile(spriteFileFormat, file);
      final Alert success = new Alert(Alert.AlertType.INFORMATION);
      success.setTitle("Saving successful");
      success.setHeaderText("Successfully saved your sprite.");
      success.setContentText("You can now open, modify, share and distribute your file " + file.getAbsolutePath() + ".");
      success.showAndWait();
    } catch (final IOException ioException) {
      final Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Sprite save error");
      alert.setHeaderText("An exception occured while saving your sprite to a file.");
      alert.setContentText("The exception message is: [" + ioException.getMessage() + "].\n"
          + "If you encounter this problem frequently or think this shouldn't have happened, please "
          + "look for known bugs at https://github.com/alttpj/MemeforceHunt/issues or restart MemeforceHunt "
          + "via the command line and check the console output.");
      alert.initModality(Modality.WINDOW_MODAL);
      alert.showAndWait();
    }
  }

  private boolean queryMetadata() {
    // show metadata edit GUI.
    final MetadataWindow metadataWindow = new MetadataWindow(this.getScene().getWindow());
    metadataWindow.displayNameProperty().bindBidirectional(this.displayNameProperty);
    metadataWindow.authorNameProperty().bindBidirectional(this.authorNameProperty);
    metadataWindow.descriptionProperty().bindBidirectional(this.descriptionProperty);
    metadataWindow.showAndWait();

    if (metadataWindow.canceledProperty().get()) {
      final Alert success = new Alert(Alert.AlertType.INFORMATION);
      success.setTitle("Saving canceled");
      success.setHeaderText("You canceled the saving process.");
      success.setContentText("Your progress is not lost until you close MemeforceHunt.");
      success.showAndWait();

      return false;
    }

    return true;
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

      if (index < unpackedBytes1616.length / 2 && (index / 8) % 2 != 0) {
        // 2nd  tile
        unpackedTiles[indexInTile + 64] = unpackedBytes1616[index];
      }

      if (index >= unpackedBytes1616.length / 2 && (index / 8) % 2 == 0) {
        // 3rd  tile
        unpackedTiles[indexInTile + 128] = unpackedBytes1616[index];
      }

      if (index >= unpackedBytes1616.length / 2 && (index / 8) % 2 != 0) {
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
