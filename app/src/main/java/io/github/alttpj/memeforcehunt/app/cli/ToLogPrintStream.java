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

package io.github.alttpj.memeforcehunt.app.cli;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToLogPrintStream extends PrintStream {

  private final Logger logger;
  private final Level level;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public ToLogPrintStream(final Logger logger, final Level level) {
    super(OutputStream.nullOutputStream(), true, StandardCharsets.UTF_8);
    this.logger = logger;
    this.level = level;
  }

  @Override
  public void println(final String stringToPrint) {
    this.logger.log(this.level, stringToPrint);
  }

  @Override
  public void println(final Object objectToPrint) {
    if (objectToPrint == null) {
      return;
    }

    this.logger.log(this.level, objectToPrint.toString());
  }

  @Override
  public void print(final String stringToPrint) {
    this.logger.log(this.level, stringToPrint);
  }

  @Override
  public void print(final Object objectToPrint) {
    if (objectToPrint == null) {
      return;
    }

    this.logger.log(this.level, objectToPrint.toString());
  }
}
