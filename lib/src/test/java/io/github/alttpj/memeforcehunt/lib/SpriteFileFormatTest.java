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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.github.alttpj.memeforcehunt.common.value.ULID;

import org.junit.jupiter.api.Test;

public class SpriteFileFormatTest {

  @Test
  public void testSpriteFileFormat_uid_immutable() {
    // given
    final ImmutableSpriteFileFormat fileFormat = ImmutableSpriteFileFormat.builder()
        .displayName("")
        .data(new byte[TileFactory.BYTES_PER_TILE * 4])
        .colorPaletteName("GREEN")
        .authorName("")
        .build();

    // when
    final ULID.Value ulid = fileFormat.getUlid();

    // then
    assertThat(ulid, is(fileFormat.getUlid()));
  }
}
