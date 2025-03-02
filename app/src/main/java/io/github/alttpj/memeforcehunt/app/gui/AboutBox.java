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

import io.github.alttpj.memeforcehunt.app.gui.actions.StaticGuiActions;
import io.github.alttpj.memeforcehunt.lib.AlttpRomPatcher;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutBox extends Stage implements Initializable {

  private final HostServices hostServices;

  @FXML
  private Button prefButtonOk;

  @FXML
  private Text textNameVersion;

  @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
  public AboutBox(final Window owner, final HostServices hostServices) {
    this.hostServices = hostServices;
    this.initOwner(owner);
    this.initModality(Modality.WINDOW_MODAL);

    // fmxl
    final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
        "/io/github/alttpj/memeforcehunt/app/gui/AboutBox.fxml"));
    fxmlLoader.setController(this);

    try {
      final Parent root1 = fxmlLoader.load();
      this.initModality(Modality.WINDOW_MODAL);
      this.setTitle("About");
      final JMetro jMetro = new JMetro(Style.DARK);
      final Scene scene = new Scene(root1);
      jMetro.setScene(scene);
      this.setScene(scene);
    } catch (final IOException ioException) {
      throw new RuntimeException(ioException);
    }
  }

  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    final String version = AlttpRomPatcher.getVersion();

    this.textNameVersion.setText("MemeforceHunt v" + version);
  }

  @FXML
  public void onAboutOk(final ActionEvent event) {
    this.close();
  }

  @FXML
  public void onLinkClick(final ActionEvent event) {
    StaticGuiActions.tryOpenTeamPage(getHostServices());
  }

  public HostServices getHostServices() {
    return this.hostServices;
  }
}
