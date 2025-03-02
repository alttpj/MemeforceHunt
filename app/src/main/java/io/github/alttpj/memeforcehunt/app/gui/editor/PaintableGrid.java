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

import static java.util.stream.Collectors.toList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.alttpj.library.image.palette.Palette;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;

@SuppressFBWarnings("EI_EXPOSE_REP")
public class PaintableGrid extends Pane {

  private static final Logger LOG = Logger.getLogger(PaintableGrid.class.getCanonicalName());

  private final int rows;
  private final int columns;

  private final ColourableCell[][] cells;

  private final BooleanProperty editableProperty = new SimpleBooleanProperty(true);

  public PaintableGrid(final int columns, final int rows) {
    super();
    this.columns = columns;
    this.rows = rows;

    this.cells = new ColourableCell[rows][columns];
  }

  /**
   * Add cell to array and to the UI.
   */
  public void add(final ColourableCell cell, final int column, final int row) {
    this.cells[row][column] = cell;

    getChildren().add(cell);
  }

  public List<ColourableCell> getCells() {
    return Arrays.stream(this.cells)
        .flatMap(Arrays::stream)
        .collect(toList());
  }

  public ColourableCell getCell(final int column, final int row) {
    return this.cells[row][column];
  }

  public void paletteSwap(final Palette palette) {
    Arrays.stream(this.cells)
        .flatMap(Arrays::stream)
        .forEach(cell -> {
          final Color colorFromPalette = getColorFromPalette(palette, cell.getSnesPaletteIndex());
          cell.paint(colorFromPalette);
        });
  }

  private Color getColorFromPalette(final Palette palette, final int paletteIndex) {
    return Color.rgb(palette.getColor(paletteIndex)[0], palette.getColor(paletteIndex)[1],
        palette.getColor(paletteIndex)[2], palette.getColor(paletteIndex)[3] == 255 ? 1.0 : 0.0);
  }


  public BooleanProperty editableProperty() {
    return this.editableProperty;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "PaintableGrid{", "}")
        .add("rows=" + this.rows)
        .add("columns=" + this.columns)
        .add("height=" + this.getHeight())
        .add("width=" + this.getWidth())
        .add("cells=" + Arrays.toString(this.cells))
        .toString();
  }

}
