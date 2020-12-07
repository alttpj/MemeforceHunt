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

import static java.util.Collections.emptyList;

import io.github.alttpj.memeforcehunt.common.value.ItemSprite;
import io.github.alttpj.memeforcehunt.common.value.ULID;
import io.github.alttpj.memeforcehunt.lib.impl.YamlProvider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alttpj.library.image.palette.Palette;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Optional;

public final class SpriteFileFormatFactory {

  private static final ObjectMapper YAML = YamlProvider.getObjectMapper();

  private SpriteFileFormatFactory() {
    // util
  }

  public static SpriteFileFormat fromFile(final File inputFile) throws IOException {
    try (final InputStream fis = Files.newInputStream(inputFile.toPath())) {
      return YAML.readValue(fis, AbstractSpriteFileFormat.class);
    }
  }

  public static SpriteFileFormat fromItemSprite(final ItemSprite source) {
    final String paletteName = source.getPalette().name();
    final String author = source.getAuthor();

    throw new UnsupportedOperationException("not implemented");
  }

  public static void saveFile(final SpriteFileFormat spriteFileFormat, final File targetFile) throws IOException {
    try (final OutputStream os = Files.newOutputStream(targetFile.toPath(),
        StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
         final OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
      save(spriteFileFormat, osw);
    }
  }

  public static void save(final SpriteFileFormat spriteFileFormat, final Writer writer) throws IOException {
    YAML.writeValue(writer, spriteFileFormat);
  }

  public static SpriteFileFormat create(final String displayName, final String authorName, final byte[] data, final Palette palette) {
    return create(new ULID().nextValue(), displayName, authorName, data, palette, null, emptyList());
  }

  public static SpriteFileFormat create(final ULID.Value ulid,
                                        final String displayName, final String authorName, final byte[] data, final Palette palette,
                                        final String description, final Collection<String> tags) {
    if (data.length != TileFactory.BYTES_PER_TILE * 4) {
      throw new IllegalArgumentException("Expected data length to be " + TileFactory.BYTES_PER_TILE * 4 + " bytes.");
    }

    return ImmutableSpriteFileFormat.builder()
        .displayName(displayName)
        .data(data)
        .colorPaletteName(palette.getName())
        .authorName(authorName)
        .description(Optional.ofNullable(description).filter(descr -> !descr.isEmpty()).filter(descr -> !descr.isBlank()))
        .addAllTags(tags)
        .ulid(ulid)
        .build();
  }
}
