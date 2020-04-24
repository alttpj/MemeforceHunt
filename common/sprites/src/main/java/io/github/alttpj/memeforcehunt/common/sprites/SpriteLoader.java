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

package io.github.alttpj.memeforcehunt.common.sprites;

import io.github.alttpj.memeforcehunt.common.value.ItemPalette;
import io.github.alttpj.memeforcehunt.common.value.SpritemapWithSkin;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class SpriteLoader {

  private final InputStream is;
  private final AtomicBoolean isAlreadyLoaded = new AtomicBoolean();
  private List<SpritemapWithSkin> loaded;

  public SpriteLoader(final InputStream is) {
    this.is = is;
  }

  public List<SpritemapWithSkin> load() {
    if (this.isAlreadyLoaded.get()) {
      return this.loaded;
    }
    final Yaml yaml = new Yaml();
    @SuppressWarnings("unchecked") final Map<String, Object> spriteMap = yaml.loadAs(this.is, Map.class);
    @SuppressWarnings("unchecked") final List<Object> sprites = (List<Object>) spriteMap.get("sprites");

    this.loaded = sprites.stream()
        .filter(Objects::nonNull)
        .map(this::loadSprite)
        .collect(Collectors.toList());

    return this.loaded;
  }

  private SpritemapWithSkin loadSprite(final Object sprite) {
    if (!(sprite instanceof Map)) {
      throw new IllegalArgumentException("invalid yaml, not a map: " + sprite);
    }

    @SuppressWarnings("unchecked") final Map<String, String> spriteMap = (Map<String, String>) sprite;

    final String spriteName = Objects.requireNonNull(spriteMap.get("spriteName"), "field 'spriteName' may not be missing.").trim();
    final String description = Optional.ofNullable(spriteMap.get("description")).orElse(spriteName);

    return new ShippedSpritemapWithSkin(
        spriteMap.get("id"),
        spriteName,
        description,
        spriteMap.get("author"),
        spriteMap.get("uri"),
        spriteMap.get("preview"),
        ItemPalette.valueOf(spriteMap.get("palette")));
  }
}
