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

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public final class ImageHelper {

  private ImageHelper() {
    // util.
  }

  public static Image generateTransparentPattern(final int width, final int numCells) {
    final WritableImage img = new WritableImage(width, width);
    final PixelWriter pw = img.getPixelWriter();

    final Color white = Color.WHITE;

    for (int xoffset = 0; xoffset < width; xoffset++) {
      for (int yoffset = 0; yoffset < width; yoffset++) {
        pw.setColor(xoffset, yoffset, white);
      }
    }

    final int cellWidth = width / (numCells * 2);

    for (int cellNumX = 0; cellNumX < numCells * 2; cellNumX++) {
      for (int cellNumY = 0; cellNumY < numCells * 2; cellNumY++) {
        if (cellNumX % 2 == 0 ^ cellNumY % 2 == 0) {
          paintCell(cellNumX, cellNumY, cellWidth, pw);
        }
      }
    }

    return img;
  }

  private static void paintCell(final int cellNumX, final int cellNumY, final int cellWidth, final PixelWriter pw) {
    for (int xcoord = 0; xcoord < cellWidth; xcoord++) {
      for (int ycoord = 0; ycoord < cellWidth; ycoord++) {
        pw.setColor(xcoord + cellNumX * cellWidth, ycoord + cellNumY * cellWidth, Color.GRAY);
      }
    }
  }

}
