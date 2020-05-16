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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.StringJoiner;

public class ColourableCell extends StackPane {

  private final int column;
  private final int row;

  private final ObjectProperty<ColorSelectorCell> painted = new SimpleObjectProperty<>();

  private final BooleanProperty editableProperty = new SimpleBooleanProperty(true);
  private final int index;

  public ColourableCell(final int column, final int row, final int index) {
    super();
    this.column = column;
    this.row = row;
    this.index = index;

    getStyleClass().add("cell");
    this.editableProperty().addListener((source, old, newVal) -> {
      if (!newVal) {
        this.getStyleClass().remove("cell");
      }
    });
    paint(new ColorSelectorCell(0, new int[] {0, 0, 0, 0}));
    bindPaint();
  }

  private void bindPaint() {
    this.painted.addListener((src, old, newVal) -> {
      if (old == newVal) {
        return;
      }
      setBackground(new Background(new BackgroundFill(newVal.getDisplayColor(), null, null)));
    });
  }

  public int getIndex() {
    return this.index;
  }

  public void paint(final ColorSelectorCell colorSelectorCell) {
    this.painted.set(colorSelectorCell);
  }

  public void paint(final Color color) {
    setBackground(new Background(new BackgroundFill(color, null, null)));
  }

  public void hoverHighlight() {
    // ensure the style is only once in the style list
    getStyleClass().remove("cell-hover-highlight");

    // add style
    getStyleClass().add("cell-hover-highlight");
  }

  public ColorSelectorCell getPainted() {
    return this.painted.get();
  }

  public ObjectProperty<ColorSelectorCell> paintedProperty() {
    return this.painted;
  }

  public byte getSnesPaletteIndex() {
    return this.painted.get().getSnesPaletteIndex();
  }

  public void hoverUnhighlight() {
    getStyleClass().remove("cell-hover-highlight");
  }

  public BooleanProperty editableProperty() {
    return this.editableProperty;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "ColourableCell{", "}")
        .add("column=" + this.column)
        .add("row=" + this.row)
        .add("positionX=" + this.getLayoutX())
        .add("positionY=" + this.getLayoutY())
        .toString();
  }

}
