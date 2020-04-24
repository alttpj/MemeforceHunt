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

package io.github.alttpj.memeforcehunt.common.value;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.ImageIcon;

public abstract class AbstractSpritemapWithSkin implements SpritemapWithSkin {

  private static final int BUFFER_SIZE = 512;
  private final ULID.Value id;
  private final String spriteName;
  private final String description;
  private final String author;
  private final ItemPalette palette;

  public AbstractSpritemapWithSkin(final String spriteId,
                                   final String spriteName,
                                   final String description,
                                   final String author,
                                   final ItemPalette palette) {
    this.id = ULID.parseULID(spriteId);
    this.spriteName = spriteName;
    this.description = description;
    this.author = author;
    this.palette = palette;
  }

  public AbstractSpritemapWithSkin(final ULID.Value spriteId,
                                   final String spriteName,
                                   final String description,
                                   final String author,
                                   final ItemPalette palette) {
    this.id = spriteId;
    this.spriteName = spriteName;
    this.description = description;
    this.author = author;
    this.palette = palette;
  }

  @Override
  public ULID.Value getId() {
    return this.id;
  }

  @Override
  public String getSpriteName() {
    return this.spriteName;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getAuthor() {
    return this.author;
  }

  @Override
  public ImageIcon getImageIcon() {
    return createImageIcon(getImage());
  }

  protected abstract BufferedImage getImage();

  protected static ImageIcon createImageIcon(final BufferedImage itemp) {
    final BufferedImage imageIconImage = new BufferedImage(32, 32, BufferedImage.TYPE_4BYTE_ABGR);
    final Graphics2D graphics2D = imageIconImage.createGraphics();

    graphics2D.scale(2, 2);
    graphics2D.drawImage(itemp, 0, 0, null);
    graphics2D.dispose();

    return new ImageIcon(imageIconImage);
  }

  @Override
  public byte getItemPalette() {
    return this.palette.getPaletteIdChest();
  }

  @Override
  public byte getPaletteOW() {
    return this.palette.getPaletteIdOverworld();
  }

  @Override
  public byte[] getData() throws IOException {
    try (final InputStream spritemapInput = getSpritemapInputStream();
         final ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      final byte[] buffer = new byte[BUFFER_SIZE];
      int readCount;

      while ((readCount = spritemapInput.read(buffer)) != -1) {
        outputStream.write(buffer, 0, readCount);
      }

      return outputStream.toByteArray();
    }
  }

  protected abstract InputStream getSpritemapInputStream();
}
