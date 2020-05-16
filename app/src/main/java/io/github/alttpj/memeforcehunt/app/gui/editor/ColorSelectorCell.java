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

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ColorSelectorCell extends Pane {
  private final byte snesPaletteIndex;
  private final Color displayColor;

  public ColorSelectorCell(final int colourNum, final int[] color) {
    this.snesPaletteIndex = (byte) colourNum;
    this.displayColor = Color.rgb(color[0], color[1], color[2], color[3] == 255 ? 1.0 : 0.0);

    setBackground(new Background(new BackgroundFill(this.displayColor, null, null)));
    setBorder(new Border(new BorderStroke(Paint.valueOf("white"), BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
    prefWidthProperty().bind(prefHeightProperty());
  }

  public byte getSnesPaletteIndex() {
    return this.snesPaletteIndex;
  }

  public Color getDisplayColor() {
    return this.displayColor;
  }
}
