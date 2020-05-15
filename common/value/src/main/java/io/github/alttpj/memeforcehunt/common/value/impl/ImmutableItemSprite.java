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
import io.github.alttpj.memeforcehunt.common.value.ULID;

import io.github.alttpj.library.image.Tile;

public class ImmutableItemSprite extends AbstractItemSprite {

  public ImmutableItemSprite(final ULID.Value spriteId, final String spriteName,
                             final String displayName, final String description, final String author,
                             final Tile[] tiles,
                             final ItemPalette palette) {
    super(spriteId, spriteName, displayName, description, author, tiles, palette);
  }

  public ImmutableItemSprite(final ULID.Value spriteId, final String spriteName, final String description, final String author,
                             final Tile[] tiles, final ItemPalette palette) {
    super(spriteId, spriteName, description, author, tiles, palette);
  }

  public ImmutableItemSprite(final ULID.Value spriteId, final String spriteName, final String description,
                             final Tile[] tiles, final ItemPalette palette) {
    super(spriteId, spriteName, description, tiles, palette);
  }

}
