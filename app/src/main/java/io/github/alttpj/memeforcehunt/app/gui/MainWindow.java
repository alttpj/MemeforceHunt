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
import io.github.alttpj.memeforcehunt.app.gui.main.DefaultSpriteTab;
import io.github.alttpj.memeforcehunt.app.gui.main.MainPane;
import io.github.alttpj.memeforcehunt.app.gui.preferences.Preferences;
import io.github.alttpj.memeforcehunt.app.gui.properties.SelectedFileProperty;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainWindow extends BorderPane implements Initializable {

  private static final FileChooser.ExtensionFilter SFC_EXTENSION_FILTER =
      new FileChooser.ExtensionFilter("SFC rom files (*.sfc)", "*.sfc");

  @FXML
  private MainPane mainPane;

  @FXML
  private MenuItem fileMenuExit;

  @FXML
  private MenuItem fileMenuCloseRom;

  @FXML
  private VBox bottomPane;

  @FXML
  private Label statusBarLabel;

  private HostServices hostServices;

  private final SelectedFileProperty selectedFileProperty = new SelectedFileProperty();

  public MainWindow() {
    // fmxl
    final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/io/github/alttpj/memeforcehunt/app/gui/MainWindow.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);

    try {
      fxmlLoader.load();
    } catch (final IOException ioException) {
      throw new RuntimeException(ioException);
    }
  }


  @Override
  public void initialize(final URL location, final ResourceBundle resources) {
    assert this.fileMenuExit != null : "fx:id=\"fileMenuExit\" was not injected: check your FXML file 'AxisFxml.fxml'.";
    this.fileMenuCloseRom.setDisable(true);
    final DefaultSpriteTab defaultSpriteTab = this.mainPane.getDefaultSpriteTab();
    defaultSpriteTab.fileProperty().bind(this.selectedFileProperty);
    this.mainPane.getRollYourOwnSpriteTab().selectedFileProperty().bind(this.selectedFileProperty);


    this.selectedFileProperty.addListener(
        (ObservableValue<?> observableValue, Object oldVal, Object newVal) -> {
          final Optional<File> selectedFile = (Optional<File>) newVal;
          if (selectedFile.isEmpty()) {
            this.fileMenuCloseRom.setDisable(true);
            this.statusBarLabel.setText("No ROM File Loaded.");
            return;
          }

          final File loadedFile = selectedFile.orElseThrow();

          this.fileMenuCloseRom.setDisable(false);
          this.statusBarLabel.setText("Loaded file [" + loadedFile.getAbsolutePath() + "].");
        });

  }

  @FXML
  public void onFileMenuExitClick(final ActionEvent event) {
    Platform.exit();
  }

  @FXML
  public void onFileMenuOpenRom(final ActionEvent actionEvent) {
    final FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open ROM File");
    fileChooser.getExtensionFilters().add(SFC_EXTENSION_FILTER);
    final File file = fileChooser.showOpenDialog(this.bottomPane.getScene().getWindow());

    if (file == null) {
      return;
    }

    this.selectedFileProperty.set(file);
  }

  @FXML
  public void onFileMenuFileClose(final ActionEvent actionEvent) {
    this.selectedFileProperty.set((File) null);
  }

  @FXML
  public void onHelpMenuUpdate(final ActionEvent actionEvent) {
    StaticGuiActions.tryOpenAboutPage(getHostServices());
  }

  @FXML
  public void onHelpMenuAbout(final ActionEvent actionEvent) {
    final AboutBox aboutBox = new AboutBox(this.getScene().getWindow(), getHostServices());

    aboutBox.showAndWait();
  }

  @FXML
  public void onEditMenuPreferences(final ActionEvent actionEvent) {
    final Preferences preferences = new Preferences(this.getScene().getWindow());

    preferences.showAndWait();
  }

  @FXML
  public void onHelp(final ActionEvent actionEvent) {
    StaticGuiActions.tryOpenHelpPage(getHostServices());
  }

  public HostServices getHostServices() {
    return this.hostServices;
  }

  public void setHostServices(final HostServices hostServices) {
    this.hostServices = hostServices;
  }
}
