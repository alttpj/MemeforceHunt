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

package io.github.alttpj.memeforcehunt.lib;

import io.github.alttpj.memeforcehunt.common.value.SpritemapWithSkin;

import io.github.alttpj.library.compress.SnesDecompressor;
import io.github.alttpj.library.image.Tile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public final class TileFactory {

  protected static final int[] TRIFORCE_TILE_POSITIONS = {44, 45, 60, 61};

  /**
   * 24 bytes per 8 x 8 x 3bpp tile. It is uncompressed, but still packed.
   */
  private static final int BYTES_PER_TILE = 24;

  private TileFactory() {
    // util
  }

  public static Tile[] fromSpritemapWithSkin(final SpritemapWithSkin spritemapWithSkin) throws IOException {
    final byte[] spritemap = spritemapWithSkin.getData();

    return fromCompressedSpritemap(spritemap);
  }

  public static Tile[] fromCompressedSpritemap(final byte[] compressedSpritemap) throws IOException {
    try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedSpritemap)) {
      final SnesDecompressor snesDecompressor = new SnesDecompressor(byteArrayInputStream);
      final byte[] decompressedSpriteMap = snesDecompressor.getDecompressed();

      return fromDecompressedSpritemap(decompressedSpriteMap);
    }
  }

  protected static Tile[] fromDecompressedSpritemap(final byte[] decompressedSpriteMap) {
    final Tile[] tiles = new Tile[TRIFORCE_TILE_POSITIONS.length];

    for (int tileIndex = 0; tileIndex < TRIFORCE_TILE_POSITIONS.length; tileIndex++) {
      final int offset = TRIFORCE_TILE_POSITIONS[tileIndex] * BYTES_PER_TILE;
      final byte[] tileBytes = new byte[BYTES_PER_TILE];
      System.arraycopy(decompressedSpriteMap, offset, tileBytes, 0, BYTES_PER_TILE);
      final Tile tile = () -> tileBytes;
      tiles[tileIndex] = tile;
    }

    return tiles;
  }

  public static int[] getDefaultOffsets() {
    final int[] offsets = new int[TRIFORCE_TILE_POSITIONS.length];
    final int[] tilePositions = TRIFORCE_TILE_POSITIONS;

    for (int tileNumber = 0; tileNumber < tilePositions.length; tileNumber++) {
      final int tilePosition = tilePositions[tileNumber];
      offsets[tileNumber] = tilePosition * BYTES_PER_TILE;
    }

    return offsets;
  }
}
