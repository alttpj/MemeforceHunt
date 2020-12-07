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

import io.github.alttpj.memeforcehunt.common.value.ItemPalette;
import io.github.alttpj.memeforcehunt.common.value.ULID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;

@Value.Immutable
@Value.Style(
    stagedBuilder = true,
    jdkOnly = true,
    visibility = Value.Style.ImplementationVisibility.PUBLIC
)
@JsonDeserialize(as = ImmutableJsonShippedSpritemapWithSkin.class)
abstract class AbstractJsonShippedSpritemapWithSkin {

  // - id: 01E6F3YXSW5A3AB0X4A1ACX0FN
  @JsonProperty("ulid")
  @JsonDeserialize(using = ULIDDeserializer.class)
  public abstract ULID.Value getUlid();

  //  author: kan
  @JsonProperty("author")
  public abstract Optional<String> getAuthor();

  //  spriteName: 1up
  @JsonProperty("spriteName")
  public abstract String getSpriteName();

  @JsonProperty("displayName")
  public abstract Optional<String> getDisplayName();

  //  uri: "/gfx/1up.bin"
  @JsonProperty("uri")
  public abstract URI getUri();

  //  preview: "/previews/1up.png"
  @JsonProperty("preview")
  public abstract URI getPreview();

  //  description: You have an extra life! ...If only.
  @JsonProperty("description")
  public abstract Optional<String> getDescription();

  //  created: '2020-04-21T21:38:11+02:00'
  @JsonProperty("created")
  public abstract Optional<Instant> getCreatedAt();

  //  palette: 'GREEN'
  @JsonProperty("palette")
  public abstract ItemPalette getItemPalette();

}
