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

package io.github.alttpj.memeforcehunt.app.gui.editor;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MetadataWindow extends Stage implements Initializable {

  @FXML
  private TextField displayNameTextField;

  @FXML
  private TextField authorTextField;

  @FXML
  private TextField descriptionTextField;

  @FXML
  private Button metadataSaveButton;

  private final BooleanProperty canceled = new SimpleBooleanProperty(false);

  private final StringProperty displayName = new SimpleStringProperty("");

  private final StringProperty authorName = new SimpleStringProperty("");

  private final StringProperty description = new SimpleStringProperty("");

  public MetadataWindow(final Window owner) {
    this.initOwner(owner);
    this.initModality(Modality.WINDOW_MODAL);

    // fmxl
    final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
        "/io/github/alttpj/memeforcehunt/app/gui/editor/Metadata.fxml"));
    fxmlLoader.setController(this);

    try {
      final Parent root1 = fxmlLoader.load();
      this.initModality(Modality.WINDOW_MODAL);
      this.setTitle("Sprite metadata");
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
    this.displayNameProperty().bindBidirectional(this.displayNameTextField.textProperty());
    this.authorNameProperty().bindBidirectional(this.authorTextField.textProperty());
    this.descriptionProperty().bindBidirectional(this.descriptionTextField.textProperty());
    this.metadataSaveButton.disableProperty().set(true);
    this.displayNameProperty().addListener((source, oldVal, newVal) -> {
      if (newVal.isBlank() || newVal.isEmpty()) {
        this.metadataSaveButton.disableProperty().set(true);
        return;
      }

      this.metadataSaveButton.disableProperty().set(false);
    });
  }

  @FXML
  public void onSaveClick(final ActionEvent clickedEvent) {
    if (getDisplayName().isBlank() || getDisplayName().isEmpty()) {
      this.canceledProperty().set(true);
    }

    this.close();
  }

  @FXML
  public void onCancelClick(final ActionEvent clickedEvent) {
    this.canceled.set(true);
    this.close();
  }

  public String getDisplayName() {
    return this.displayName.get();
  }

  public StringProperty displayNameProperty() {
    return this.displayName;
  }

  public void setDisplayName(final String displayName) {
    this.displayName.set(displayName);
  }

  public TextField getDisplayNameTextField() {
    return this.displayNameTextField;
  }

  public void setDisplayNameTextField(final TextField displayNameTextField) {
    this.displayNameTextField = displayNameTextField;
  }

  public TextField getAuthorTextField() {
    return this.authorTextField;
  }

  public void setAuthorTextField(final TextField authorTextField) {
    this.authorTextField = authorTextField;
  }

  public boolean isCanceled() {
    return this.canceled.get();
  }

  public BooleanProperty canceledProperty() {
    return this.canceled;
  }

  public void setCanceled(final boolean canceled) {
    this.canceled.set(canceled);
  }

  public String getAuthorName() {
    return this.authorName.get();
  }

  public StringProperty authorNameProperty() {
    return this.authorName;
  }

  public void setAuthorName(final String authorName) {
    this.authorName.set(authorName);
  }

  public String getDescription() {
    return this.description.get();
  }

  public StringProperty descriptionProperty() {
    return this.description;
  }

  public void setDescription(final String description) {
    this.description.set(description);
  }
}
