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

package io.github.alttpj.memeforcehunt.app.cli.commands;

import io.github.alttpj.memeforcehunt.app.cli.internal.SuppressForbidden;
import io.github.alttpj.memeforcehunt.common.sprites.DefaultSpritemapWithSkins;
import io.github.alttpj.memeforcehunt.common.value.SpritemapWithSkin;

import picocli.CommandLine.Command;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

@Command(name = "list", aliases = {"ls"}, description = "List available (shipped) skins.")
public class ListSkins implements Callable<Integer> {

  @Override
  public Integer call() throws Exception {
    final List<SpritemapWithSkin> values = DefaultSpritemapWithSkins.values();

    values.forEach(ListSkins::printSkin);

    return 0;
  }

  @SuppressForbidden
  private static void printSkin(final SpritemapWithSkin spritemapWithSkin) {
    final String format = String.format(Locale.ENGLISH,
        "%-16s - %-32s by %-16s",
        spritemapWithSkin.getSpriteName(),
        spritemapWithSkin.getDescription(),
        spritemapWithSkin.getAuthor()
    );

    System.out.println(format);
  }
}
