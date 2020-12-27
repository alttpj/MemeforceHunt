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

import static java.util.Collections.unmodifiableList;

import io.github.alttpj.library.image.palette.Palette;
import io.github.alttpj.library.image.palette.Palette3bpp;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.util.Arrays;
import java.util.List;

public final class ColorSelector extends GridPane {

  private final ObjectProperty<ColorSelectorCell> selectedColor = new SimpleObjectProperty<>();

  private ColorSelectorCell[] colorSelectorCells;

  private final int boxesPerRow;

  public ColorSelector() {
    super();
    this.boxesPerRow = 4;
    setColors(Palette3bpp.GREEN);
    selectColor(this.colorSelectorCells[0]);
  }

  void setColors(final Palette palette) {
    final List<int[]> colors = palette.getColors();
    this.colorSelectorCells = new ColorSelectorCell[colors.size()];

    for (int colourNum = 0; colourNum < colors.size(); colourNum++) {
      final ColorSelectorCell colorSelectorCell = new ColorSelectorCell(colourNum, palette.getColor(colourNum));
      colorSelectorCell.setOnMouseClicked(this::selectColor);
      colorSelectorCell.prefHeightProperty().set(32);
      this.colorSelectorCells[colourNum] = colorSelectorCell;
    }

    this.getChildren().clear();
    this.getChildren().addAll(this.colorSelectorCells);

    for (int cellIndex = 0; cellIndex < this.colorSelectorCells.length; cellIndex++) {
      final int row = cellIndex / this.boxesPerRow;
      final int column = cellIndex % this.boxesPerRow;
      GridPane.setConstraints(this.colorSelectorCells[cellIndex], column, row);

    }
  }

  private void selectColor(final MouseEvent event) {
    final ColorSelectorCell selectedCell = (ColorSelectorCell) event.getSource();
    selectColor(selectedCell);
  }

  private void selectColor(final ColorSelectorCell selectedCell) {
    this.selectedColor.setValue(selectedCell);
    Arrays.stream(this.colorSelectorCells).forEach(cell -> cell.getStyleClass().remove("selected"));
    selectedCell.getStyleClass().add("selected");
  }

  public ColorSelectorCell getSelectedColor() {
    return this.selectedColor.get();
  }

  public ObjectProperty<ColorSelectorCell> selectedColorProperty() {
    return this.selectedColor;
  }

  public void setSelectedColor(final ColorSelectorCell selectedColor) {
    this.selectedColor.set(selectedColor);
  }

  public List<ColorSelectorCell> getColorSelectorCells() {
    return unmodifiableList(Arrays.asList(this.colorSelectorCells));
  }
}
