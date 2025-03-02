/*
 * Copyright 2020-2025 the ALttPJ Team @ https://github.com/alttpj
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

package io.github.alttpj.memeforcehunt.common.value.impl;

import io.github.alttpj.memeforcehunt.common.value.ItemPalette;
import io.github.alttpj.memeforcehunt.common.value.ItemSprite;
import io.github.alttpj.memeforcehunt.common.value.ULID;

import io.github.alttpj.library.image.Tile;

import java.util.Arrays;
import java.util.Objects;

public record ItemSpriteRecord(
    ULID.Value spriteId,
    String spriteName,
    String displayName,
    String description,
    String author,
    Tile[] tiles,
    ItemPalette palette
)
    implements ItemSprite {

  private static final int[] TRIFORCE_TILE_POSITIONS = {44, 45, 60, 61};

  private static final int BYTES_PER_TILE = 24;

  public ItemSpriteRecord(
      final ULID.Value spriteId,
      final String spriteName,
      final String displayName,
      final String description,
      final String author,
      final Tile[] tiles,
      final ItemPalette palette) {
    Objects.requireNonNull(tiles, "tiles may not be null!");

    if (tiles.length == 0) {
      throw new IllegalArgumentException("No tiles supplied (tiles.length == 0).");
    }

    if (spriteId == null) {
      throw new NullPointerException("SpriteId may not be null!");
    }

    if (spriteName == null) {
      throw new NullPointerException("spriteName may not be null!");
    }

    if (tiles.length != getTilePositions().length) {
      throw new IllegalArgumentException("sprite tiles must have the same length as tile positions!");
    }

    if (ItemSprite.getTileOffsets(getTilePositions(), palette).length != getTilePositions().length) {
      throw new IllegalArgumentException("sprite tile offsets must have the same length as tile positions!");
    }

    for (int tileNum = 0, tiles1Length = tiles.length; tileNum < tiles1Length; tileNum++) {
      final Tile tile = tiles[tileNum];

      if (tile.getBytes().length != BYTES_PER_TILE) {
        throw new IllegalArgumentException(
            "Tile no. [" + tileNum + "] has length of [" + tile.getBytes().length + "] bytes, "
                + "but expected are exactly [" + BYTES_PER_TILE + "] bytes.");
      }
    }

    this.spriteId = spriteId;
    this.spriteName = spriteName;
    this.displayName = displayName;
    this.description = description;
    this.author = author;
    this.tiles = Arrays.copyOf(tiles, tiles.length);
    this.palette = palette;
  }

  @Override
  public ULID.Value getSpriteId() {
    return this.spriteId;
  }

  @Override
  public String getSpriteName() {
    return this.spriteName;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
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
  public Tile[] getTiles() {
    return Arrays.copyOf(this.tiles, this.tiles.length);
  }

  public Tile[] tiles() {
    return Arrays.copyOf(this.tiles, this.tiles.length);
  }

  @Override
  public int[] getTilePositions() {
    return Arrays.copyOf(TRIFORCE_TILE_POSITIONS, TRIFORCE_TILE_POSITIONS.length);
  }

  @Override
  public ItemPalette getPalette() {
    return this.palette;
  }

}
