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

package io.github.alttpj.memeforcehunt.app.gui.preferences;

import io.github.alttpj.memeforcehunt.app.config.YamlConfigurator;
import io.github.alttpj.memeforcehunt.lib.AlttpRomPatcher;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

@SuppressFBWarnings("EI_EXPOSE_REP")
public class Preferences extends Stage implements Initializable {

  @FXML
  private Button prefButtonOk;

  @FXML
  private CheckBox customMemoryEnabledCheckBox;

  @FXML
  private MultiNumberInput customMemoryAddressField;

  @FXML
  private Text memoryAddressHexDisplay;

  private final StringProperty hexString = new SimpleStringProperty();

  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "fxml")
  public Preferences(final Window owner) {
    initialize(owner);
  }

  private void initialize(final Window owner) {
    this.initOwner(owner);
    this.initModality(Modality.WINDOW_MODAL);

    // fmxl
    final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
        "/io/github/alttpj/memeforcehunt/app/gui/preferences/Preferences.fxml"));
    fxmlLoader.setController(this);

    try {
      final Parent root1 = fxmlLoader.load();
      this.initModality(Modality.WINDOW_MODAL);
      this.setTitle("Preferences");
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
    // only activate text field if the checkbox is selected.
    this.customMemoryAddressField.disableProperty().bind(this.customMemoryEnabledCheckBox.selectedProperty().not());

    // map the address input to the local hex display field.
    this.customMemoryAddressField.getHexValue().addListener((source, oldValue, newValue) -> {
      this.memoryAddressHexDisplay.setText(String.format(Locale.ENGLISH, "0x%6s", newValue).replaceAll(" ", "0"));
    });

    final YamlConfigurator yamlConfigurator = new YamlConfigurator();
    this.customMemoryEnabledCheckBox.setSelected(yamlConfigurator.useCustomPatchOffset());
    setAddress(yamlConfigurator.getCustomOffsetAddress());
  }

  public IntegerProperty getAddress() {
    return this.customMemoryAddressField.getAddress();
  }

  public void setAddress(final int newValue) {
    this.customMemoryAddressField.getAddress().set(newValue);
  }

  public String getAddressAsHex() {
    return this.memoryAddressHexDisplay.getText();
  }

  @FXML
  public void onMemoryAddressReset(final ActionEvent actionEvent) {
    setAddress(AlttpRomPatcher.DEFAULT_SPRITEMAP_OFFSET);
    this.customMemoryEnabledCheckBox.setSelected(false);
  }

  @FXML
  public void onPrefButtonApplyClick(final ActionEvent actionEvent) {
    save();
  }

  @FXML
  public void onPrefButtonCancelClick(final ActionEvent actionEvent) {
    this.close();
  }

  @FXML
  public void onPrefButtonOkClick(final ActionEvent actionEvent) {
    save();
    this.close();
  }

  public void save() {
    final YamlConfigurator yamlConfigurator = new YamlConfigurator();
    yamlConfigurator.setCustomPatchOffset(this.customMemoryEnabledCheckBox.isSelected());

    if (this.customMemoryEnabledCheckBox.isSelected()) {
      final String hexOffset = getAddressAsHex();
      yamlConfigurator.setCustomOffsetAddress(hexOffset);
    }
  }
}
