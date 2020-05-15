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

public enum ItemPalette {
  /**
   * Gree palette id.
   */
  GREEN((byte) 0x04, (byte) 0x08, 3),
  BLUE((byte) 0x02, (byte) 0x04, 3),
  RED((byte) 0x01, (byte) 0x02, 3);

  /**
   * If item is in chest, this palette ID is used.
   */
  private final byte paletteIdChest;

  /**
   * If item is lying around in the overworld, this palette ID is used.
   */
  private final byte paletteIdOverworld;

  private final int bpp;

  ItemPalette(final byte paletteIdChest, final byte paletteIdOverworld, final int bpp) {
    this.paletteIdChest = paletteIdChest;
    this.paletteIdOverworld = paletteIdOverworld;
    this.bpp = bpp;
  }

  public byte getPaletteIdChest() {
    return this.paletteIdChest;
  }

  public byte getPaletteIdOverworld() {
    return this.paletteIdOverworld;
  }


  public int getBitsPerPixel() {
    return 3;
  }
}
