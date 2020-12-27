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

import io.github.alttpj.memeforcehunt.common.value.ItemPalette;
import io.github.alttpj.memeforcehunt.common.value.ItemSprite;
import io.github.alttpj.memeforcehunt.common.value.ItemSpriteFactory;
import io.github.alttpj.memeforcehunt.common.value.SpritemapWithSkin;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.github.alttpj.library.compress.SnesCompressor;
import io.github.alttpj.library.compress.SnesDecompressor;
import io.github.alttpj.library.image.Tile;
import io.github.alttpj.library.image.TiledSprite;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringJoiner;

public class AlttpRomPatcher {

  public static final int DEFAULT_SPRITEMAP_OFFSET = 0x18A800;
  public static final int PAL_LOC = 0x104FE4;
  public static final int PAL_OW = 0x10126E;

  protected static final int MAX_SPRITEMAP_SIZE = 1023;
  private int offset;
  private int paletteLocationChest;
  private int paletteLocationOverworld;

  public AlttpRomPatcher() {
    this.offset = DEFAULT_SPRITEMAP_OFFSET;
    this.paletteLocationChest = PAL_LOC;
    this.paletteLocationOverworld = PAL_OW;
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN,PATH_TRAVERSAL_OUT",
      justification = "patching user supplied ROM file"
  )
  public void patchROM(final String romTarget, final SpritemapWithSkin spritemapWithSkin) throws IOException {
    final byte[] romStream = readRom(romTarget);

    writeSkin(romStream, spritemapWithSkin);

    writeRom(romTarget, romStream);
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN,PATH_TRAVERSAL_OUT",
      justification = "patching user supplied ROM file"
  )
  public void patchROM(final String romTarget, final TiledSprite itemSprite) throws IOException {
    final byte[] romStream = readRom(romTarget);

    writeTiledSprite(romStream, itemSprite);

    writeRom(romTarget, romStream);
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_OUT",
      justification = "patching user supplied ROM file"
  )
  private void writeRom(final String romTarget, final byte[] romStream) throws IOException {
    try (final FileOutputStream fsOut = new FileOutputStream(romTarget)) {
      fsOut.write(romStream, 0, romStream.length);
    }
  }

  /**
   * Extracts teh four tiles of the new triforce sprite from the old format ({@link SpritemapWithSkin}) and
   * writes them into the spritemap from the ROM.
   *
   * @param romStream         the romStream to write bytes into.
   * @param spritemapWithSkin the old format, only the four item sprite tiles will be read from this.
   * @throws IOException error reading tiles or reading rom..
   */
  protected void writeSkin(final byte[] romStream, final SpritemapWithSkin spritemapWithSkin) throws IOException {
    // extract only the 4 triforce tiles
    final Tile[] tiles = TileFactory.fromSpritemapWithSkin(spritemapWithSkin);
    // convert to new format
    final ItemSprite itemSprite = ItemSpriteFactory.fromSpritemapWithSkin(spritemapWithSkin, tiles);

    // defer to new method call.
    writeItemSprite(romStream, itemSprite);
  }

  /**
   * Writes an item sprite to the rom, but in contrast to {@link #writeSkin(byte[], SpritemapWithSkin)}, it will
   * first read out the existing item palette at offset {@link #getOffset()}, uncompress it, insert the uncompressed tiles,
   * recompress it, and then write it back.
   *
   * <p>This way, the other sprites in the spritemap are preserved, e.g. silver arrows, swords and bombs.</p>
   *
   * @param romStream  the romStream to read the item palette from and write to.
   * @param itemSprite the item sprite to write.
   * @throws IOException problem reading or writing the rom Stream.
   */
  protected void writeItemSprite(final byte[] romStream, final ItemSprite itemSprite) throws IOException {
    final byte[] decompressedSpritemapFromRom = extractDecompressedSpritemapFromRom(romStream);

    doWriteItemSpriteIntoDecompressedSpritemap(itemSprite, decompressedSpritemapFromRom);

    // compress
    final byte[] compressedNewSpritemap = compressSpritemap(decompressedSpritemapFromRom);

    doWriteCompressedSpritemapIntoRom(romStream, compressedNewSpritemap);
    romStream[getPaletteLocationChest()] = itemSprite.getPalette().getPaletteIdChest();
    romStream[getPaletteLocationOverworld()] = itemSprite.getPalette().getPaletteIdOverworld();
  }

  /**
   * Writes an item sprite to the rom, but in contrast to {@link #writeSkin(byte[], SpritemapWithSkin)}, it will
   * first read out the existing item palette at offset {@link #getOffset()}, uncompress it, insert the uncompressed tiles,
   * recompress it, and then write it back.
   *
   * <p>This way, the other sprites in the spritemap are preserved, e.g. silver arrows, swords and bombs.</p>
   *
   * @param romStream   the romStream to read the item palette from and write to.
   * @param tiledSprite the item sprite to write.
   * @throws IOException problem reading or writing the rom Stream.
   */
  protected void writeTiledSprite(final byte[] romStream, final TiledSprite tiledSprite) throws IOException {
    final byte[] decompressedSpritemapFromRom = extractDecompressedSpritemapFromRom(romStream);

    doWriteTiledSpriteIntoDecompressedSpritemap(tiledSprite, decompressedSpritemapFromRom);

    // compress
    final byte[] compressedNewSpritemap = compressSpritemap(decompressedSpritemapFromRom);

    doWriteCompressedSpritemapIntoRom(romStream, compressedNewSpritemap);
    final ItemPalette itemPalette = ItemPalette.valueOf(tiledSprite.getPalette().getName());
    romStream[getPaletteLocationChest()] = itemPalette.getPaletteIdChest();
    romStream[getPaletteLocationOverworld()] = itemPalette.getPaletteIdOverworld();
  }

  private void doWriteCompressedSpritemapIntoRom(final byte[] romStream, final byte[] compressedNewSpritemap) throws IOException {
    if (compressedNewSpritemap.length > MAX_SPRITEMAP_SIZE) {
      throw new IOException(
          "Skin too large! "
              + "Max is [" + MAX_SPRITEMAP_SIZE + "], "
              + "but supplied skin contains [" + compressedNewSpritemap.length + "] bytes.");
    }

    final int pos = getOffset();
    // write back
    System.arraycopy(new byte[MAX_SPRITEMAP_SIZE], 0, romStream, pos, MAX_SPRITEMAP_SIZE);
    // write graphics
    System.arraycopy(compressedNewSpritemap, 0, romStream, pos, compressedNewSpritemap.length);
  }

  private void doWriteItemSpriteIntoDecompressedSpritemap(final ItemSprite itemSprite, final byte[] decompressedSpritemapFromRom) {
    final Tile[] tiles = itemSprite.getTiles();
    final int[] tileOffsets = itemSprite.getTileOffsets();

    for (int tileIndex = 0; tileIndex < tiles.length; tileIndex++) {
      final byte[] tileToWrite = tiles[tileIndex].getBytes();
      final int tileOffset = tileOffsets[tileIndex];
      System.arraycopy(tileToWrite, 0, decompressedSpritemapFromRom, tileOffset, tileToWrite.length);
    }
  }

  private void doWriteTiledSpriteIntoDecompressedSpritemap(final TiledSprite tiledSprite, final byte[] decompressedSpritemapFromRom) {
    final Tile[] tiles = tiledSprite.getTiles();
    final int[] tileOffsets = TileFactory.getDefaultOffsets();

    for (int tileIndex = 0; tileIndex < tiles.length; tileIndex++) {
      final byte[] tileToWrite = tiles[tileIndex].getBytes();
      final int tileOffset = tileOffsets[tileIndex];
      System.arraycopy(tileToWrite, 0, decompressedSpritemapFromRom, tileOffset, tileToWrite.length);
    }
  }

  private byte[] compressSpritemap(final byte[] decompressedSpritemapFromRom) throws IOException {
    try (final SnesCompressor snesCompressor = new SnesCompressor(new ByteArrayInputStream(decompressedSpritemapFromRom))) {
      return snesCompressor.getCompressed().toByteArray();
    }
  }

  private byte[] extractDecompressedSpritemapFromRom(final byte[] romStream) throws IOException {
    final int pos = getOffset();

    try (final ByteArrayInputStream romInputStream = new ByteArrayInputStream(romStream, pos, MAX_SPRITEMAP_SIZE)) {
      final SnesDecompressor snesDecompressor = new SnesDecompressor(romInputStream);

      return snesDecompressor.getDecompressed();
    }
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "patching user supplied ROM file"
  )
  private byte[] readRom(final String romTarget) throws IOException {
    final byte[] romStream;

    try (final FileInputStream fsInput = new FileInputStream(romTarget)) {
      final int size = (int) fsInput.getChannel().size();
      romStream = new byte[size];
      final int read = fsInput.read(romStream);
      if (read != size) {
        throw new IllegalStateException("Unexpected end of input.");
      }
      fsInput.getChannel().position(0);
    }

    return romStream;
  }

  public static String getVersion() {
    try (final InputStream versionProps = AlttpRomPatcher.class.getResourceAsStream("/constants/version.properties")) {
      final Properties properties = new Properties();
      properties.load(versionProps);

      return properties.getProperty("MemforceHunt.version", "UNKNOWN");
    } catch (final IOException ioException) {
      return "UNKNOWN";
    }
  }

  public int getOffset() {
    return this.offset;
  }

  public void setOffset(final int offset) {
    if (offset <= -1) {
      // ignore
      return;
    }
    this.offset = offset;
  }

  public int getPaletteLocationChest() {
    return this.paletteLocationChest;
  }

  public void setPaletteLocationChest(final int paletteLocationChest) {
    this.paletteLocationChest = paletteLocationChest;
  }

  public int getPaletteLocationOverworld() {
    return this.paletteLocationOverworld;
  }

  public void setPaletteLocationOverworld(final int paletteLocationOverworld) {
    this.paletteLocationOverworld = paletteLocationOverworld;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", "AlttpRomPatcher{", "}")
        .add("offset=" + this.offset)
        .add("paletteLocationChest=" + this.paletteLocationChest)
        .add("paletteLocationOverworld=" + this.paletteLocationOverworld)
        .toString();
  }
}
