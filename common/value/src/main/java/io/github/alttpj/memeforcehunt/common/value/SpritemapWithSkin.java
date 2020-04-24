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

import java.io.IOException;
import javax.swing.ImageIcon;

/**
 * Contains a spritemap which can be patched.
 *
 * <p>Tile positions of the triforce sprite (starting at 0):</p>
 * <ul>
 *   <li>44</li>
 *   <li>45</li>
 *   <li>60</li>
 *   <li>61</li>
 * </ul>
 */
public interface SpritemapWithSkin {

  ULID.Value getId();

  /**
   * Returns a short, non-changing identifier.
   *
   * @return a short human-readable ID.
   */
  String getSpriteName();

  String getDescription();

  String getAuthor();

  /**
   * Returns a 32x32 image icon.
   *
   * @return a 32x32 image icon.
   */
  ImageIcon getImageIcon();

  byte[] getData() throws IOException;

  byte getItemPalette();

  byte getPaletteOW();
}
