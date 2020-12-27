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

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YamlConfigurator extends AbstractOsConfigurationFile {

  private static final Logger LOG = java.util.logging.Logger.getLogger(YamlConfigurator.class.getCanonicalName());

  private static final ObjectMapper YAML = YamlProvider.getObjectMapper();

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

    return readFromYaml("useCustomPatchOffset", Boolean.FALSE);
  }

  private <T> T readFromYaml(final String fieldName, final T defaultValue) {
    try (final InputStream yamlInputStream = Files.newInputStream(getConfigFilePath(), StandardOpenOption.READ)) {
      final Map<String, Object> yamlConfig = (Map<String, Object>) YAML.readValue(yamlInputStream, Map.class);

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

    final Map<String, Object> yamlConfig = new ConcurrentHashMap<>();

    try (final InputStream yamlInputStream = Files.newInputStream(getConfigFilePath(), StandardOpenOption.READ)) {
      final Map<String, Object> loadedConfig = YAML.readValue(yamlInputStream, Map.class);
      if (loadedConfig != null) {
        yamlConfig.putAll(loadedConfig);
      }
    } catch (final ClassCastException | IOException ioEx) {
      readBackupConfig(ioEx);
    }

    yamlConfig.put(fieldName, value);

    try (final OutputStream outputStream = Files.newOutputStream(getConfigFilePath(), StandardOpenOption.WRITE);
         final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
      YAML.writeValue(outputStreamWriter, yamlConfig);
    } catch (final IOException ioException) {
      LOG.log(Level.SEVERE, ioException, () -> "Unable to write config to [" + getConfigFile().getAbsolutePath() + "].");
    }
  }

  @SuppressFBWarnings(
      value = "PATH_TRAVERSAL_IN",
      justification = "relative to known config, not user supplie"
  )
  private void readBackupConfig(final Exception ioEx) {
    LOG.log(Level.SEVERE, ioEx, () -> "Unable to read config from [" + getConfigFilePath() + "].");
    final Path destination = Paths.get(getConfigFile().getAbsolutePath() + ".old");

    // make a backup before (over-)writing.
    try {
      Files.copy(getConfigFilePath(), destination, StandardCopyOption.REPLACE_EXISTING);
    } catch (final IOException copyIoEx) {
      LOG.log(Level.SEVERE, copyIoEx,
          () -> String.format(Locale.ENGLISH, "Unable to write backup from [%s] to [%s].",
              getConfigFilePath(),
              destination));
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
