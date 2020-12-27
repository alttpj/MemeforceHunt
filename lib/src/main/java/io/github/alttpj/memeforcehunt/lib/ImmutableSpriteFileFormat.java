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
import io.github.alttpj.memeforcehunt.lib.impl.ULIDDeserializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

@SuppressFBWarnings(
    value = "EQ_UNUSUAL",
    justification = "generated"
)
public class ImmutableSpriteFileFormat implements SpriteFileFormat, Comparable<SpriteFileFormat> {

  private static final Set<String> ALLOWED_PALETTE_NAMES = Set.of("GREEN", "RED", "BLUE");

  private static final int SPRITE_DATA_LENGTH = TileFactory.BYTES_PER_TILE * 4;

  private final ULID.Value ulid;
  private final Instant creationDate;
  private final String description;
  private final String displayName;
  private final String authorName;
  private final byte[] data;
  private final String colorPaletteName;
  private final List<String> tags;

  public ImmutableSpriteFileFormat(
      final ULID.Value ulid,
      final Instant creationDate,
      final String description,
      final String displayName,
      final String authorName,
      final byte[] data,
      final String colorPaletteName,
      final Collection<String> tags
  ) {
    this.ulid = ulid;
    this.creationDate = creationDate;
    this.description = description;
    this.displayName = displayName;
    this.authorName = authorName;
    this.data = Arrays.copyOf(data, data.length);
    this.colorPaletteName = colorPaletteName;
    this.tags = List.copyOf(tags);
    check();
  }

  @Override
  @JsonDeserialize(using = ULIDDeserializer.class)
  public ULID.Value ulid() {
    return this.ulid;
  }

  @Override
  @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  public Instant creationDate() {
    return this.creationDate;
  }

  @Override
  @JsonProperty("description")
  public Optional<String> description() {
    return Optional.ofNullable(this.description);
  }

  @Override
  @JsonProperty("displayName")
  public String displayName() {
    return this.displayName;
  }

  @Override
  @JsonProperty("author")
  public String authorName() {
    return Optional.ofNullable(this.authorName)
        .orElse("unknown");
  }

  @Override
  @JsonProperty("data")
  public byte[] data() {
    return Arrays.copyOf(this.data, this.data.length);
  }

  @Override
  @JsonProperty("palette")
  public String colorPaletteName() {
    return this.colorPaletteName;
  }

  @Override
  @JsonProperty("tags")
  public List<String> tags() {
    return List.copyOf(this.tags);
  }

  public final void check() {
    if (this.data.length != SPRITE_DATA_LENGTH) {
      throw new IllegalArgumentException("Expected sprite length to be " + SPRITE_DATA_LENGTH
          + ", but got " + this.data.length + " bytes.");
    }
  }

  @Override
  public int compareTo(final SpriteFileFormat other) {
    return Comparator.comparing(SpriteFileFormat::ulid)
        .compare(this, other);
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    final ImmutableSpriteFileFormat that = (ImmutableSpriteFileFormat) other;
    return this.ulid.equals(that.ulid)
        && this.creationDate.equals(that.creationDate)
        && Objects.equals(this.description, that.description)
        && this.displayName.equals(that.displayName)
        && Objects.equals(this.authorName, that.authorName)
        && Arrays.equals(this.data, that.data)
        && this.colorPaletteName.equals(that.colorPaletteName)
        && this.tags.equals(that.tags);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(
        this.ulid,
        this.creationDate,
        this.description,
        this.displayName,
        this.authorName,
        this.colorPaletteName,
        this.tags);
    result = 31 * result + Arrays.hashCode(this.data);
    return result;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ImmutableSpriteFileFormat.class.getSimpleName() + "[", "]")
        .add("super=" + super.toString())
        .add("ulid=" + this.ulid)
        .add("creationDate=" + this.creationDate)
        .add("description='" + this.description + "'")
        .add("displayName='" + this.displayName + "'")
        .add("authorName='" + this.authorName + "'")
        .add("data=" + Arrays.toString(this.data))
        .add("colorPaletteName='" + this.colorPaletteName + "'")
        .add("tags=" + this.tags)
        .toString();
  }
}
