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

package io.github.alttpj.memeforcehunt.app.gui;

import io.github.alttpj.memeforcehunt.lib.AlttpRomPatcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;

public class MemeForceHuntJavaFX extends Application {

  private static final String LIB_VERSION = AlttpRomPatcher.getVersion();

  public static void main(final String[] args) {
    launch(args);
  }

  @Override
  public void start(final Stage primaryStage) throws IOException {
    final MainWindow root = new MainWindow();
    root.setHostServices(this.getHostServices());

    final JMetro jMetro = new JMetro(Style.DARK);
    final Scene scene = new Scene(root, 720, 572);
    jMetro.setScene(scene);

    primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/triforce piece.png")));
    primaryStage.setTitle("Memforce Hunt v" + LIB_VERSION);
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
