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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;

import java.util.StringJoiner;

@SuppressFBWarnings("EI_EXPOSE_REP")
public final class SpritePreview extends SpriteGridCanvas {

  private final IntegerProperty wantedHeight = new SimpleIntegerProperty(64);
  private final IntegerProperty wantedWidth = new SimpleIntegerProperty(64);

  public SpritePreview() {
    super(false);
    initialize();
  }

  public void initialize() {
    setAlignment(Pos.TOP_LEFT);
    setAlignment(getBgImage(), Pos.TOP_LEFT);
    getPaintableGrid().editableProperty().set(false);
    getPaintableGrid().setManaged(false);
  }


  public boolean getEditable() {
    return getPaintableGrid().editableProperty().get();
  }

  public void setEditable(final boolean value) {
    getPaintableGrid().editableProperty().set(value);
  }

  @Override
  public void setHeight(final double width) {
    super.setHeight(width);

    this.wantedHeight.set(Double.valueOf(width).intValue());

    this.minHeight(width);
    this.maxHeight(width);
    this.prefHeight(width);

    getPaintableGrid().setMinHeight(width);
    getPaintableGrid().setMaxHeight(width);

    getBgImage().imageProperty().set(ImageHelper.generateTransparentPattern(Double.valueOf(width).intValue(), 16));
  }

  @Override
  public void setWidth(final double height) {
    super.setWidth(height);

    this.wantedWidth.set(Double.valueOf(height).intValue());

    this.minWidth(height);
    this.maxWidth(height);
    this.prefWidth(height);

    this.getPaintableGrid().setMinWidth(height);
    this.getPaintableGrid().setMaxWidth(height);

    getBgImage().imageProperty().set(ImageHelper.generateTransparentPattern(Double.valueOf(height).intValue(), 16));
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "SpritePreview{", "}")
        .toString();
  }
}
