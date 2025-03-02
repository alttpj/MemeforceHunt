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

package io.github.alttpj.memeforcehunt.common.sprites.impl;

import io.github.alttpj.memeforcehunt.common.value.SpritemapWithSkin;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpriteLoader {

  private static final ObjectMapper YAML = YamlMapper.getObjectMapper();

  private final InputStream is;
  private final AtomicBoolean isAlreadyLoaded = new AtomicBoolean();
  private List<SpritemapWithSkin> loaded;

  public SpriteLoader(final InputStream is) {
    this.is = is;
  }

  public List<SpritemapWithSkin> load() {
    if (this.isAlreadyLoaded.get()) {
      return List.copyOf(this.loaded);
    }

    try {
      final AbstractJsonShippedSpritemapWithSkin[] sprites = YAML.readValue(this.is, AbstractJsonShippedSpritemapWithSkin[].class);

      this.loaded = Arrays.stream(sprites)
          .map(this::toSpriteMapWithSkin)
          .toList();

      return List.copyOf(this.loaded);
    } catch (final IOException ioException) {
      throw new UnsupportedOperationException("not yet implemented: [${CLASS_NAME}::${METHOD_NAME}].", ioException);
    }
  }

  private SpritemapWithSkin toSpriteMapWithSkin(final AbstractJsonShippedSpritemapWithSkin sprite) {
    final String spriteName = sprite.getSpriteName();

    return new ShippedSpritemapWithSkin(
        sprite.getUlid().toString(),
        spriteName,
        sprite.getDisplayName().orElse(spriteName),
        sprite.getDescription().or(sprite::getDisplayName).orElse(spriteName),
        sprite.getAuthor().orElse("unknown"),
        sprite.getUri().toString(),
        sprite.getPreview().toString(),
        sprite.getItemPalette()
    );
  }
}
