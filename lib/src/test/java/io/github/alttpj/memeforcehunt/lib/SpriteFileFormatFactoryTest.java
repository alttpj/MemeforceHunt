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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.alttpj.library.image.palette.Palette;
import org.junit.jupiter.api.Test;

public class SpriteFileFormatFactoryTest {

  @Test
  public void testData() {
    // given
    // -- empty data
    final byte[] data = new byte[0];

    // when
    // then
    final IllegalArgumentException illegalArgumentException = assertThrows(
        IllegalArgumentException.class,
        () -> SpriteFileFormatFactory.create("asd", "me", data, mock(Palette.class))
    );

    assertTrue(illegalArgumentException.getMessage().contains("data"));
  }

  @Test
  public void testCreate() {
    // given
    // -- data
    final byte[] data = new byte[TileFactory.BYTES_PER_TILE * 4];
    final Palette palette = mock(Palette.class);
    when(palette.getName()).thenReturn("GREEN");

    // when
    final SpriteFileFormat spriteFileFormat = SpriteFileFormatFactory.create("asd", "me", data, palette);

    // then
    assertAll(
        () -> assertTrue(spriteFileFormat.getDescription().isEmpty()),
        () -> assertEquals("me", spriteFileFormat.getAuthorName()),
        () -> assertEquals(palette.getName(), spriteFileFormat.getColorPaletteName())
    );
  }
}
