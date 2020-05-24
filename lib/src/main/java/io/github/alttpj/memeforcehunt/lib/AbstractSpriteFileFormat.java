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


import io.github.alttpj.memeforcehunt.common.value.ULID;

import org.immutables.value.Value;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Value.Immutable
abstract class AbstractSpriteFileFormat implements SpriteFileFormat, Comparable<SpriteFileFormat> {

  private static final List<String> ALLOWED_PALETTE_NAMES = List.of("GREEN", "RED", "BLUE");

  private static final int SPRITE_DATA_LENGTH = TileFactory.BYTES_PER_TILE * 4;

  @Override
  @Value.Default
  public ULID.Value getUlid() {
    return new ULID().nextValue();
  }

  @Override
  @Value.Derived
  public Instant getCreationDate() {
    return Instant.ofEpochMilli(getUlid().timestamp());
  }

  @Override
  @Value.Default
  public Optional<String> getDescription() {
    return Optional.empty();
  }

  @Value.Check
  public AbstractSpriteFileFormat normalize() {
    if (this.getDescription().isPresent() && this.getDescription().orElseThrow().isBlank()) {
      return ImmutableSpriteFileFormat.copyOf(this)
          .withDescription(Optional.empty());
    }

    final String upperCasePaletteName = getColorPaletteName().toUpperCase(Locale.ENGLISH);
    if (!upperCasePaletteName.equals(getColorPaletteName())) {
      return ImmutableSpriteFileFormat.copyOf(this)
          .withColorPaletteName(upperCasePaletteName);
    }

    if (!ALLOWED_PALETTE_NAMES.contains(upperCasePaletteName)) {
      return ImmutableSpriteFileFormat.copyOf(this)
          .withColorPaletteName("GREEN");
    }

    if (getData().length != SPRITE_DATA_LENGTH) {
      throw new IllegalArgumentException("Expected sprite length to be " + SPRITE_DATA_LENGTH
          + ", but got " + getData().length + " bytes.");
    }

    return this;
  }

  @Override
  public int compareTo(final SpriteFileFormat other) {
    return Comparator.comparing(SpriteFileFormat::getUlid)
        .compare(this, other);
  }
}
