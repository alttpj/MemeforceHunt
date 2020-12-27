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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SpriteGridCanvas extends StackPane {

  private static final Logger LOG = Logger.getLogger(SpriteGridCanvas.class.getCanonicalName());

  private ImageView bgImage = new ImageView(ImageHelper.generateTransparentPattern(512, 16));

  private final PaintableGrid paintableGrid = new PaintableGrid(16, 16);

  private final DoubleProperty maxHeightWidth = new SimpleDoubleProperty();

  private final ObjectProperty<ColorSelectorCell> selectedColorSelectorCell = new SimpleObjectProperty<>();

  public SpriteGridCanvas() {
    super();
    initialize(true);
  }

  public SpriteGridCanvas(final boolean editable) {
    super();
    initialize(editable);
  }

  protected final void initialize(final boolean editable) {
    setAlignment(Pos.CENTER);
    setAlignment(this.bgImage, Pos.CENTER);
    getChildren().add(this.bgImage);
    getChildren().add(this.paintableGrid);
    this.paintableGrid.setManaged(false);
    this.paintableGrid.editableProperty().set(editable);

    this.widthProperty().addListener(this::updateMaxHeightWidth);
    this.heightProperty().addListener(this::updateMaxHeightWidth);

    this.bgImage.fitWidthProperty().bind(this.maxHeightWidth);
    this.bgImage.fitHeightProperty().bind(this.maxHeightWidth);
    this.paintableGrid.prefWidthProperty().bind(this.maxHeightWidth);
    this.paintableGrid.prefHeightProperty().bind(this.maxHeightWidth);

    this.paintableGrid.layoutXProperty().unbind();
    this.paintableGrid.layoutYProperty().unbind();

    this.paintableGrid.layoutXProperty().bind(this.bgImage.xProperty());
    this.paintableGrid.layoutYProperty().bind(this.bgImage.yProperty());

    addCells(editable);

    // on size change
    this.paintableGrid.widthProperty().addListener((source, old, newVal) -> {
      final Image newBg = ImageHelper.generateTransparentPattern(newVal.intValue(), 16);
      this.bgImage.imageProperty().set(newBg);
    });
  }

  protected void addCells(final boolean editable) {
    final GridMouseActions gma = new GridMouseActions(this.selectedColorSelectorCell);

    // fill grid
    int counter = 0;
    for (int row = 0; row < 16; row++) {
      for (int column = 0; column < 16; column++) {
        final ColourableCell cell = new ColourableCell(column, row, counter++);
        cell.prefWidthProperty().bind(this.maxHeightWidth.divide(16));
        cell.prefHeightProperty().bind(this.maxHeightWidth.divide(16));
        cell.layoutXProperty().bind(this.bgImage.layoutXProperty().add(cell.widthProperty().multiply(column)));
        cell.layoutYProperty().bind(this.bgImage.layoutYProperty().add(cell.heightProperty().multiply(row)));
        cell.editableProperty().bind(getPaintableGrid().editableProperty());
        if (editable) {
          gma.makePaintable(cell);
        }
        LOG.log(Level.FINER, "Adding cell: " + cell);
        this.paintableGrid.add(cell, column, row);
      }
    }
  }

  private void updateMaxHeightWidth(final ObservableValue<? extends Number> observableValue, final Number old, final Number newValue) {
    final double max = Math.min(getWidth(), getHeight());
    this.maxHeightWidth.set(max);
  }

  public ImageView getBgImage() {
    return this.bgImage;
  }

  public void setBgImage(final ImageView bgImage) {
    this.bgImage = bgImage;
  }

  public PaintableGrid getPaintableGrid() {
    return this.paintableGrid;
  }

  public ColorSelectorCell getSelectedColorSelectorCell() {
    return this.selectedColorSelectorCell.get();
  }

  public ObjectProperty<ColorSelectorCell> selectedColorSelectorCellProperty() {
    return this.selectedColorSelectorCell;
  }

  public void setSelectedColorSelectorCell(final ColorSelectorCell selectedColorSelectorCell) {
    this.selectedColorSelectorCell.set(selectedColorSelectorCell);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "SpriteGridCanvas{", "}")
        .add("height=" + this.getHeight())
        .add("width=" + this.getWidth())
        .add("maxHeightWidth=" + this.maxHeightWidth.get())
        .add("bgImage=" + this.bgImage)
        .add("paintableGrid=" + this.paintableGrid)
        .toString();
  }

}
