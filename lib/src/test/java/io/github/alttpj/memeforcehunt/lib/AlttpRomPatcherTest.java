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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alttpj.memeforcehunt.common.value.AbstractSpritemapWithSkin;
import io.github.alttpj.memeforcehunt.common.value.ItemPalette;
import io.github.alttpj.memeforcehunt.common.value.ULID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AlttpRomPatcherTest {

  @Test
  @Disabled
  public void shouldRejectLongByteArrays() throws IOException {
    // given big sprite map
    final byte[] bytes = new byte[AlttpRomPatcher.MAX_SPRITEMAP_SIZE * 2];
    final AbstractSpritemapWithSkin spritemap =
        new AbstractSpritemapWithSkin(new ULID().nextULID(), "test", "test", "author", ItemPalette.GREEN) {
          @Override
          public BufferedImage getImage() {
            return null;
          }

          @Override
          protected InputStream getSpritemapInputStream() {
            return new ByteArrayInputStream(bytes);
          }
        };
    final byte[] fakeRom = new byte[4096];
    fakeRom[4095] = (byte) 0xFF;

    // when
    final AlttpRomPatcher alttpRomPatcher = new AlttpRomPatcher();

    // then
    alttpRomPatcher.writeSkin(fakeRom, spritemap);
  }

  @Test
  public void shouldNotSetNegativeValues() {
    // given a positive value
    final AlttpRomPatcher alttpRomPatcher = new AlttpRomPatcher();
    final int defaultOffset = alttpRomPatcher.getOffset();

    // when setting a bogus value
    alttpRomPatcher.setOffset(-1);

    // then assume the old value is set
    assertEquals(defaultOffset, alttpRomPatcher.getOffset());
  }

}
