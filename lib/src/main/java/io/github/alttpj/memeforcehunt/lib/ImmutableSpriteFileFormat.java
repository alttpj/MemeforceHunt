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
import io.github.alttpj.memeforcehunt.lib.impl.InstantDeserializer;
import io.github.alttpj.memeforcehunt.lib.impl.InstantSerializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record ImmutableSpriteFileFormat(
    ULID.Value ulid,
    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    Instant creationDate,
    @JsonProperty("description")
    Optional<String> description,
    @JsonProperty("displayName")
    String displayName,
    @JsonProperty("author")
    String authorName,
    @JsonProperty("data")
    byte[] data,
    @JsonProperty("palette")
    String colorPaletteName,
    @JsonProperty("tags")
    List<String> tags
)
    implements SpriteFileFormat, Comparable<SpriteFileFormat> {

  private static final Set<String> ALLOWED_PALETTE_NAMES = Set.of("GREEN", "RED", "BLUE");
  private static final int SPRITE_DATA_LENGTH = TileFactory.BYTES_PER_TILE * 4;

  public ImmutableSpriteFileFormat {
    data = Arrays.copyOf(data, data.length);
    tags = List.copyOf(tags);

    Objects.requireNonNull(data, "Data must not be null.");

    if (data.length != SPRITE_DATA_LENGTH) {
      throw new IllegalArgumentException("Expected sprite length to be " + SPRITE_DATA_LENGTH
          + ", but got " + data.length + " bytes.");
    }
  }

  @Override
  public Optional<String> description() {
    return this.description;
  }

  @Override
  public String authorName() {
    return Optional.ofNullable(this.authorName).orElse("unknown");
  }

  @Override
  public byte[] data() {
    return Arrays.copyOf(this.data, this.data.length);
  }

  @Override
  public List<String> tags() {
    return List.copyOf(this.tags);
  }

  @Override
  public int compareTo(final SpriteFileFormat other) {
    return Comparator.comparing(SpriteFileFormat::ulid)
        .compare(this, other);
  }
}
