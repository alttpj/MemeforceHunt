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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressFBWarnings("EI_EXPOSE_REP")
public class MultiNumberInput extends VBox implements Initializable {

  private static final Pattern HEX = Pattern.compile("[a-fA-F0-9]*");

  private final HexToIntTextListener hexToIntTextListener = new HexToIntTextListener();

  private final IntegerProperty address = new SimpleIntegerProperty(0);

  private final StringProperty addressHex = new SimpleStringProperty("0x000000");

  @FXML
  private TextField memoryOffset;

  @FXML
  private ToggleGroup inputTypeSelect;

  @FXML
  private RadioButton radioHex;

  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "fxml")
  public MultiNumberInput() {
    // fmxl
    final FXMLLoader fxmlLoader =
        new FXMLLoader(getClass().getResource("/io/github/alttpj/memeforcehunt/app/gui/preferences/MultiNumberInput.fxml"));
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
    this.memoryOffset.lengthProperty().addListener((source, oldVal, newVal) -> {
      if (newVal.intValue() > 6) {
        final String cutText = this.memoryOffset.getText().substring(0, 6);
        this.memoryOffset.setText(cutText);
      }
    });
    this.memoryOffset.textProperty().addListener(this.hexToIntTextListener);
    // bind address updates to hex address updates
    this.address.addListener((source, oldVal, newVal) -> this.addressHex.set(Integer.toHexString((int) newVal)));
    this.address.addListener((source, oldVal, newVal) -> this.memoryOffset.textProperty().set(Integer.toHexString((int) newVal)));

    this.inputTypeSelect.selectedToggleProperty().addListener((source, oldVal, newVal) -> {
      final RadioButton selectedRadioButton = (RadioButton) newVal;
      // remove all listeners
      this.memoryOffset.textProperty().removeListener(this.hexToIntTextListener);

      if (selectedRadioButton == this.radioHex) {
        this.memoryOffset.textProperty().addListener(this.hexToIntTextListener);
      }
    });
  }

  public StringProperty getHexValue() {
    return this.addressHex;
  }

  public IntegerProperty getAddress() {
    return this.address;
  }

  class HexToIntTextListener implements ChangeListener<String> {

    @Override
    public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
      final Matcher matcher = HEX.matcher(newValue);
      // whole text must match
      if (!matcher.matches()) {
        MultiNumberInput.this.address.set(0);
        MultiNumberInput.this.memoryOffset.textProperty().set(oldValue);
        return;
      }

      final String upperCase = newValue.toUpperCase(Locale.ENGLISH);
      if (!newValue.equals(upperCase)) {
        MultiNumberInput.this.memoryOffset.textProperty().set(upperCase);
      }

      final int hexAsInt = Integer.parseInt(newValue, 16);

      MultiNumberInput.this.address.set(hexAsInt);
    }
  }
}
