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

import io.github.alttpj.library.image.palette.Palette;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class SpriteFileFormatFactory {

  private SpriteFileFormatFactory() {
    // util
  }

  public static SpriteFileFormat fromFile(final File inputFile) throws IOException {
    final Yaml yaml = new Yaml();

    try (final InputStream fis = Files.newInputStream(inputFile.toPath())) {
      final Map<String, Object> loaded = yaml.load(fis);

      return ImmutableSpriteFileFormat.builder()
          .displayName((String) loaded.get("displayName"))
          .authorName(Optional.ofNullable(loaded.get("author"))
              .map(auth -> (String) auth)
              .orElse("unknonwn"))
          .data((byte[]) loaded.get("data"))
          .colorPaletteName((String) loaded.getOrDefault("palette", "GREEN"))
          .addAllTags(parseTags(loaded))
          .ulid(ULID.parseULID((String) loaded.get("ulid")))
          .description(Optional.ofNullable((String) loaded.get("description")))
          .build();
    }
  }

  private static Iterable<String> parseTags(final Map<String, Object> loaded) {
    final Object tags = loaded.get("tags");
    if (tags == null) {
      return emptyList();
    }

    if (tags instanceof Iterable) {
      final Iterable<?> tagsIter = (Iterable<?>) tags;

      return StreamSupport.stream(tagsIter.spliterator(), false)
          .filter(String.class::isInstance)
          .map(tag -> (String) tag)
          .collect(Collectors.toUnmodifiableList());
    }

    if (tags instanceof String) {
      return Arrays.stream(((String) tags).split(","))
          .map(String::trim)
          .collect(Collectors.toUnmodifiableList());
    }

    // unreadable tags.
    return emptyList();
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
    final DumperOptions dumperOptions = new DumperOptions();
    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    dumperOptions.setAllowUnicode(true);
    dumperOptions.setExplicitStart(false);
    dumperOptions.setExplicitEnd(false);
    final Yaml yaml = new Yaml(dumperOptions);

    final Map<String, Object> output = toMap(spriteFileFormat);

    final String yamlString = yaml.dumpAs(output, Tag.MAP, DumperOptions.FlowStyle.BLOCK);
    writer.append(yamlString);
  }

  private static Map<String, Object> toMap(final SpriteFileFormat spriteFileFormat) {
    final Map<String, Object> output = new LinkedHashMap<>();
    output.put("ulid", spriteFileFormat.getUlid().toString());
    output.put("displayName", spriteFileFormat.getDisplayName());
    final String authorName = spriteFileFormat.getAuthorName();
    if (!authorName.isBlank() && !"unknown".equals(authorName)) {
      output.put("author", authorName);
    }
    output.put("data", spriteFileFormat.getData());
    output.put("palette", spriteFileFormat.getColorPaletteName());
    spriteFileFormat.getDescription().ifPresent(desc -> output.put("description", desc));
    if (!spriteFileFormat.getTags().isEmpty()) {
      output.put("tags", spriteFileFormat.getTags());
    }
    output.put("timestamp", spriteFileFormat.getCreationDate().toString());

    return output;
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
        .authorName(authorName)
        .data(data)
        .colorPaletteName(palette.getName())
        .description(Optional.ofNullable(description).filter(descr -> !descr.isEmpty()).filter(descr -> !descr.isBlank()))
        .addAllTags(tags)
        .ulid(ulid)
        .build();
  }
}
