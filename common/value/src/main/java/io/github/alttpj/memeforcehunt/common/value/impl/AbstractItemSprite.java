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

package io.github.alttpj.memeforcehunt.common.value.impl;

import io.github.alttpj.memeforcehunt.common.value.ItemPalette;
import io.github.alttpj.memeforcehunt.common.value.ItemSprite;
import io.github.alttpj.memeforcehunt.common.value.ULID;

import io.github.alttpj.library.image.Tile;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

public abstract class AbstractItemSprite implements ItemSprite {

  private static final int[] TRIFORCE_TILE_POSITIONS = {44, 45, 60, 61};

  private static final int BYTES_PER_TILE = 24;

  private final ULID.Value spriteId;
  private final String spriteName;
  private final String displayName;
  private final String description;
  private final String author;
  private final Tile[] tiles;
  private final ItemPalette palette;

  public AbstractItemSprite(final ULID.Value spriteId,
                            final String spriteName,
                            final String displayName,
                            final String description,
                            final String author,
                            final Tile[] tiles,
                            final ItemPalette palette) {
    this.spriteId = spriteId;
    this.spriteName = spriteName;
    this.displayName = displayName;
    this.description = description;
    this.author = author;
    this.tiles = Arrays.copyOf(tiles, tiles.length);
    this.palette = palette;
    check();
  }

  public AbstractItemSprite(final ULID.Value spriteId,
                            final String spriteName,
                            final String description,
                            final String author,
                            final Tile[] tiles,
                            final ItemPalette palette) {
    this(spriteId, spriteName, null, description, author, tiles, palette);
  }

  public AbstractItemSprite(final ULID.Value spriteId,
                            final String spriteName,
                            final String description,
                            final Tile[] tiles,
                            final ItemPalette palette) {
    this(spriteId, spriteName, null, description, null, tiles, palette);
  }

  private void check() {
    if (this.tiles == null) {
      throw new NullPointerException("tiles may not be null!");
    }

    if (this.tiles.length == 0) {
      throw new IllegalArgumentException("No tiles supplied (tiles.length == 0).");
    }

    if (this.spriteId == null) {
      throw new NullPointerException("SpriteId may not be null!");
    }

    if (this.spriteName == null) {
      throw new NullPointerException("spriteName may not be null!");
    }

    if (this.tiles.length != getTilePositions().length) {
      throw new IllegalArgumentException("sprite tiles must have the same length as tile positions!");
    }

    if (getTileOffsets().length != getTilePositions().length) {
      throw new IllegalArgumentException("sprite tile offsets must have the same length as tile positions!");
    }

    for (int tileNum = 0, tiles1Length = this.tiles.length; tileNum < tiles1Length; tileNum++) {
      final Tile tile = this.tiles[tileNum];

      if (tile.getBytes().length != BYTES_PER_TILE) {
        throw new IllegalArgumentException(
            "Tile no. [" + tileNum + "] has length of [" + tile.getBytes().length + "] bytes, "
                + "but expected are exactly [" + BYTES_PER_TILE + "] bytes.");
      }
    }
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

  @Override
  public int[] getTilePositions() {
    return Arrays.copyOf(TRIFORCE_TILE_POSITIONS, TRIFORCE_TILE_POSITIONS.length);
  }

  @Override
  public ItemPalette getPalette() {
    return this.palette;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    final AbstractItemSprite that = (AbstractItemSprite) other;
    return this.spriteId.equals(that.spriteId)
        && this.spriteName.equals(that.spriteName)
        && Objects.equals(this.displayName, that.displayName)
        && Objects.equals(this.description, that.description)
        && Objects.equals(this.author, that.author)
        && Arrays.equals(this.tiles, that.tiles)
        && this.palette == that.palette;
  }

  @Override
  public int hashCode() {
    final int result = Objects.hash(this.spriteId, this.spriteName, this.displayName, this.description, this.author, this.palette);

    return 31 * result + Arrays.hashCode(this.tiles);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "AbstractSpritemapWithSkin{", "}")
        .add("spriteId=" + this.spriteId)
        .add("spriteName='" + this.spriteName + "'")
        .add("displayName='" + this.displayName + "'")
        .add("description='" + this.description + "'")
        .add("author='" + this.author + "'")
        .add("tiles=" + Arrays.toString(this.tiles))
        .add("palette=" + this.palette)
        .toString();
  }
}
