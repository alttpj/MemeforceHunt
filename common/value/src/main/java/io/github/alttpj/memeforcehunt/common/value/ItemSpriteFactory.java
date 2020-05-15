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

import io.github.alttpj.memeforcehunt.common.value.impl.ImmutableItemSprite;

import io.github.alttpj.library.image.Tile;

import java.util.Arrays;
import java.util.Objects;

public final class ItemSpriteFactory {

  private ItemSpriteFactory() {
    // util
  }

  public static ItemSprite fromSpritemapWithSkin(final SpritemapWithSkin spritemapWithSkin, final Tile[] tiles) {
    Objects.requireNonNull(spritemapWithSkin, "spritemapWithSkin may not be null");
    Objects.requireNonNull(tiles, "tiles may not be null");

    final ItemPalette palette = fromPaletteBytes(spritemapWithSkin.getItemPalette(), spritemapWithSkin.getPaletteOW());

    return new ImmutableItemSprite(
        spritemapWithSkin.getId(),
        spritemapWithSkin.getSpriteName(),
        spritemapWithSkin.getDisplayName(),
        spritemapWithSkin.getDescription(),
        spritemapWithSkin.getAuthor(),
        tiles,
        palette);
  }

  protected static ItemPalette fromPaletteBytes(final byte itemPaletteChest, final byte itemPaletteOverworld) {
    return Arrays.stream(ItemPalette.values())
        .filter(palette -> palette.getPaletteIdChest() == itemPaletteChest && palette.getPaletteIdOverworld() == itemPaletteOverworld)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
            "No item palette has bytes chest[" + itemPaletteChest + "], ov[" + itemPaletteOverworld + "]."));
  }
}
