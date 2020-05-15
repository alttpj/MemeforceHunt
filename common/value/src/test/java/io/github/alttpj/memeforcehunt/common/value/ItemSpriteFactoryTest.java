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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import io.github.alttpj.library.image.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ItemSpriteFactoryTest {

  @Test
  public void testItemPaletteFromBytes() {
    // given
    final byte chest = (byte) 0x04;
    final byte overworld = (byte) 0x08;

    // when
    final ItemPalette itemPalette = ItemSpriteFactory.fromPaletteBytes(chest, overworld);

    // then
    assertEquals(ItemPalette.GREEN, itemPalette);
  }


  @Test
  public void testNpeSpritemap() {

    // when
    final NullPointerException nullPointerException = Assertions.assertThrows(NullPointerException.class,
        () -> ItemSpriteFactory.fromSpritemapWithSkin(null, null));

    // then
    assertThat(nullPointerException.getMessage(), containsString("spritemap"));
  }

  @Test
  public void testNpeTiles() {
    // given
    final SpritemapWithSkin spritemap = mock(SpritemapWithSkin.class);

    // when
    final NullPointerException nullPointerException = Assertions.assertThrows(NullPointerException.class,
        () -> ItemSpriteFactory.fromSpritemapWithSkin(spritemap, null));

    // then
    assertThat(nullPointerException.getMessage(), containsString("tiles"));
  }

  @Test
  public void testConversion() {
    // given
    final AbstractSpritemapWithSkin spritemap =
        new AbstractSpritemapWithSkin(new ULID().nextULID(), "test", "test desc", "me", ItemPalette.GREEN) {

          @Override
          public BufferedImage getImage() {
            return null;
          }

          @Override
          protected InputStream getSpritemapInputStream() {
            return null;
          }
        };
    final Tile[] tiles = {
        () -> new byte[24],
        () -> new byte[24],
        () -> new byte[24],
        () -> new byte[24]
    };

    // when
    final ItemSprite itemSprite = ItemSpriteFactory.fromSpritemapWithSkin(spritemap, tiles);

    // then
    assertAll(
        () -> assertEquals(spritemap.getId(), itemSprite.getSpriteId())
    );
  }
}
