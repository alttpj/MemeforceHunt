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

package io.github.alttpj.memeforcehunt.app.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YamlConfigurator extends AbstractOsConfigurationFile {

  private static final Logger LOG = java.util.logging.Logger.getLogger(YamlConfigurator.class.getCanonicalName());

  /**
   * Offset not set.
   */
  private static final int NO_PATCHOFFSET_OPTION_SET = -1;

  public YamlConfigurator() {
    super();
  }


  public boolean useCustomPatchOffset() {
    if (!isUsable()) {
      return false;
    }

    return readFromYaml("useCustomPatchOffset", false);
  }

  private <T> T readFromYaml(final String fieldName, final T defaultValue) {
    final Yaml yaml = YamlProvider.createYaml();
    try (final InputStream yamlInputStream = Files.newInputStream(getConfigFilePath(), StandardOpenOption.READ)) {
      final Map<String, Object> yamlConfig = (Map<String, Object>) yaml.load(yamlInputStream);

      if (yamlConfig == null) {
        return defaultValue;
      }

      //noinspection unchecked
      return (T) yamlConfig.get(fieldName);
    } catch (final ClassCastException | IOException ioEx) {
      LOG.log(Level.SEVERE, ioEx,
          () -> String.format(Locale.ENGLISH, "Unable to read config from [%s].", getConfigFile().getAbsolutePath()));

      return defaultValue;
    }
  }

  public void setCustomPatchOffset(final boolean useCustomOffset) {
    writeField("useCustomPatchOffset", useCustomOffset);
  }

  private void writeField(final String fieldName, final Object value) {
    if (!isUsable()) {
      return;
    }

    final Yaml yaml = YamlProvider.createYaml();
    final Map<String, Object> yamlConfig = new ConcurrentHashMap<>();

    try (final InputStream yamlInputStream = Files.newInputStream(getConfigFilePath(), StandardOpenOption.READ)) {
      final Map<String, Object> loadedConfig = yaml.load(yamlInputStream);
      if (loadedConfig != null) {
        yamlConfig.putAll(loadedConfig);
      }
    } catch (final ClassCastException | IOException ioEx) {
      LOG.log(Level.SEVERE, ioEx, () -> "Unable to read config from [" + getConfigFilePath() + "].");
      final Path destination = new File(getConfigFile().getAbsolutePath() + ".old").toPath();

      // make a backup before (over-)writing.
      try {
        Files.copy(getConfigFilePath(), destination);
      } catch (final IOException copyIoEx) {
        LOG.log(Level.SEVERE, copyIoEx,
            () -> String.format(Locale.ENGLISH, "Unable to write backup from [%s] to [%s].",
                getConfigFilePath(),
                destination));
      }
    }

    yamlConfig.put(fieldName, value);

    try (final OutputStream outputStream = Files.newOutputStream(getConfigFilePath(), StandardOpenOption.WRITE);
         final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
      yaml.dump(yamlConfig, outputStreamWriter);
      outputStreamWriter.flush();
    } catch (final IOException ioException) {
      LOG.log(Level.SEVERE, ioException, () -> "Unable to write config to [" + getConfigFile().getAbsolutePath() + "].");
    }
  }


  public int getCustomOffsetAddress() {
    if (!isUsable()) {
      return NO_PATCHOFFSET_OPTION_SET;
    }

    String offset = readFromYaml("offset", "" + NO_PATCHOFFSET_OPTION_SET);
    if (offset == null) {
      return NO_PATCHOFFSET_OPTION_SET;
    }

    if (offset.startsWith("0x")) {
      offset = offset.substring(2);
    }

    try {
      return Integer.parseInt(offset, 16);
    } catch (final NumberFormatException nfEx) {
      LOG.log(Level.WARNING, "Invalid field 'offset' was written in yaml file: [" + offset + "].", nfEx);
      writeField("offset", 0);
      return NO_PATCHOFFSET_OPTION_SET;
    }
  }

  public void setCustomOffsetAddress(final String offsetAddressAsHex) {
    writeField("offset", offsetAddressAsHex);
  }
}
