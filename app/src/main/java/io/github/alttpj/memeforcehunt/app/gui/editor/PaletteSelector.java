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

import io.github.alttpj.library.image.palette.Palette;
import io.github.alttpj.library.image.palette.Palette3bpp;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;

import java.util.Optional;

public class PaletteSelector extends FlowPane {

  private final ObjectProperty<Palette> selectedPalette = new SimpleObjectProperty<>();

  public PaletteSelector() {
    final ToggleGroup toggleGroup = new ToggleGroup();
    final PaletteRadioButton greenRadio = new PaletteRadioButton(Palette3bpp.GREEN, toggleGroup, this.selectedPalette);
    final PaletteRadioButton redRadio = new PaletteRadioButton(Palette3bpp.BLUE, toggleGroup, this.selectedPalette);
    final PaletteRadioButton blueRadio = new PaletteRadioButton(Palette3bpp.RED, toggleGroup, this.selectedPalette);

    getChildren().add(greenRadio);
    getChildren().add(redRadio);
    getChildren().add(blueRadio);

    greenRadio.setSelected(true);
    setPadding(new Insets(5.0));
    setHgap(5.0);
    setVgap(5.0);
  }

  public Palette getSelectedPalette() {
    return this.selectedPalette.get();
  }

  public ObjectProperty<Palette> selectedPaletteProperty() {
    return this.selectedPalette;
  }

  public void select(final Palette palette) {
    final Optional<PaletteRadioButton> selectableRadioButton = getChildren().stream()
        .filter(PaletteRadioButton.class::isInstance)
        .map(PaletteRadioButton.class::cast)
        .filter(radioPaletteButton -> radioPaletteButton.getPalette().equals(palette))
        .findAny();

    selectableRadioButton.ifPresent(radioPaletteButton -> radioPaletteButton.selectedProperty().set(true));
    selectableRadioButton.ifPresent(radioPaletteButton -> this.selectedPalette.set(radioPaletteButton.getPalette()));
  }

  static class PaletteRadioButton extends RadioButton {
    private final Palette palette;

    public PaletteRadioButton(final Palette palette3bpp, final ToggleGroup toggleGroup, final ObjectProperty<Palette> selectedPalette) {
      this.palette = palette3bpp;
      this.setWrapText(false);
      this.setText(palette3bpp.getName());
      this.setToggleGroup(toggleGroup);
      this.selectedProperty().addListener(createSelectedChangelistener(selectedPalette));
    }

    public Palette getPalette() {
      return this.palette;
    }

    private ChangeListener<Boolean> createSelectedChangelistener(final ObjectProperty<Palette> selectedPalette) {
      return (source, old, newValue) -> {
        if (!newValue) {
          return;
        }

        selectedPalette.set(this.getPalette());
      };
    }
  }
}
