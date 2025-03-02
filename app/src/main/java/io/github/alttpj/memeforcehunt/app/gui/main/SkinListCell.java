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

package io.github.alttpj.memeforcehunt.app.gui.main;

import io.github.alttpj.memeforcehunt.common.value.SpritemapWithSkin;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.awt.image.BufferedImage;
import java.net.URL;

@SuppressFBWarnings("EI_EXPOSE_REP")
public class SkinListCell extends ListCell<SpritemapWithSkin> {

  private static final Insets DEFAULT_PADDING = new Insets(0.0d, 1.0d, 0.0d, 5.0d);

  private static final URL FONTS = SkinListCell.class.getResource("/io/github/alttpj/memeforcehunt/app/gui/fonts.css");

  public SkinListCell() {
    graphicTextGapProperty().set(15.0);
    setTextAlignment(TextAlignment.LEFT);
    setPadding(DEFAULT_PADDING);
  }

  @Override
  protected void updateItem(final SpritemapWithSkin item, final boolean empty) {
    super.updateItem(item, empty);

    if (item == null) {
      return;
    }

    setGraphic(new SkinListCellGraphic(item));
  }

  static class SkinListCellGraphic extends HBox {

    private final SpritemapWithSkin item;

    public SkinListCellGraphic(final SpritemapWithSkin item) {
      getStylesheets().add(FONTS.toExternalForm());
      this.item = item;
      setSpacing(15.0d);
      setAlignment(Pos.CENTER_LEFT);
      draw();
    }

    private void draw() {
      // left: draw item
      final BufferedImage bufferedImage = this.item.getImage();
      final WritableImage fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
      final ImageView drawnItem = new ImageView(fxImage);
      drawnItem.setPreserveRatio(true);
      drawnItem.setSmooth(false);
      drawnItem.setFitWidth(32.0);
      drawnItem.setFitHeight(32.0);
      getChildren().add(drawnItem);

      // right: Multi-Line text
      final VBox rightHandCellPart = new VBox();
      final String spriteDisplayName = this.item.getDisplayName();
      final Text spriteNameLabel = new Text(spriteDisplayName);
      spriteNameLabel.getStyleClass().add("bold");
      final Text authorLabel = new Text(" by " + this.item.getAuthor());
      authorLabel.getStyleClass().add("italic");
      rightHandCellPart.getChildren().add(new HBox(spriteNameLabel, authorLabel));
      rightHandCellPart.getChildren().add(new Label(this.item.getDescription()));
      final Text idLabel = new Text("ID: " + this.item.getId());
      idLabel.getStyleClass().add("monospaced");
      rightHandCellPart.getChildren().add(idLabel);
      getChildren().add(rightHandCellPart);
    }

  }
}
