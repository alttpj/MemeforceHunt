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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SpriteFileFormat extends Comparable<SpriteFileFormat> {

  /**
   * Each sprite has a uniquely generated ULID.
   *
   * <p>It will be used for sorting and identification.</p>
   *
   * @return the ULID
   */
  ULID.Value ulid();

  /**
   * A display name which will be shown upon loading. Should not be too long and just describe in one or two words
   * what this sprite is all about.
   *
   * <p></p>
   *
   * <p>Examples:</p>
   * <ul>
   *   <li>Pegasus Boots</li>
   *   <li>{@literal [B]}</li>
   *   <li>Pile of …</li>
   * </ul>
   *
   * <p></p>
   *
   * <p>Not so well chosen display names:</p>
   * <ul>
   *   <li>A very long sentence which should go into the description instead, because it describes the contents.</li>
   *   <li> (empty String)</li>
   * </ul>
   *
   * @return the display name.
   */
  String displayName();

  /**
   * Name of the author. This can be a nickname, a real name or name and e-mail (just as you please).<br>
   *
   * <p></p>
   *
   * <p>If you want to include your e-mail, use this format: {@code John Doe <john.doe@example.invalid}.</p>
   *
   * @return The author name.
   */
  String authorName();

  /**
   * A longer description of the item (about 1 sentence, max 2), usually includes a funny pun.
   *
   * @return an optional description of the item.
   */
  Optional<String> description();

  /**
   * When this item sprite was created. Usually derived from the ULID.
   *
   * @return creation date.
   */
  Instant creationDate();

  /**
   * A base64 encoded tile data string. It is the whole four tiles concatinated and then base64'd.
   *
   * @return the bytes contained in the base64 string.
   */
  byte[] data();

  /**
   * Name of the Color Palette.
   *
   * <p></p>
   *
   * <p><strong>Valid values:</strong></p>
   * <ul>
   *   <li>GREEN</li>
   *   <li>RED</li>
   *   <li>BLUE</li>
   * </ul>
   *
   * <p>The implementation <strong>MUST</strong> return green if this data is invalid.</p>
   *
   * @return the color palette name.
   */
  String colorPaletteName();

  /**
   * Optional (default empty) tags for searching.
   *
   * <p></p>
   *
   * <p>Examples for pegasus boots:</p>
   * <ul>
   *   <li>red boots</li>
   *   <li>red shoes</li>
   *   <li>running shoes</li>
   *   <li>red trainers</li>
   *   <li>red sneakers</li>
   * </ul>
   *
   * @return tags for searching.
   */
  List<String> tags();
}
