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

import static java.util.Collections.unmodifiableList;

import io.github.alttpj.memeforcehunt.common.value.SpritemapWithSkin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class DefaultSpritemapWithSkins {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultSpritemapWithSkins.class);

  private static final String SPRITE_FILE = "/sprites/sprites.yaml";

  private static final List<SpritemapWithSkin> DEFAULT_SPRITEMAPS = loadDefaultSpritemaps();

  private DefaultSpritemapWithSkins() {
    // util.
  }

  private static List<SpritemapWithSkin> loadDefaultSpritemaps() {
    final List<SpritemapWithSkin> sprites = new ArrayList<>();

    try (final InputStream spriteYmlIS = DefaultSpritemapWithSkins.class.getResourceAsStream(SPRITE_FILE)) {
      final SpriteLoader loader = new SpriteLoader(spriteYmlIS);
      sprites.addAll(loader.load());
    } catch (final IOException ioEx) {
      LOG.error("Unable to load default spritemaps!");
    }

    return unmodifiableList(sprites);
  }

  public static SpritemapWithSkin getByName(final String spriteName) {
    return DEFAULT_SPRITEMAPS.stream()
        .filter(spr -> spr.getSpriteName().equals(spriteName))
        .findAny().orElseThrow(NoSuchElementException::new);
  }

  public static List<SpritemapWithSkin> values() {
    return unmodifiableList(DEFAULT_SPRITEMAPS);
  }
}
