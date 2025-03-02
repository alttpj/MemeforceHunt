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

package io.github.alttpj.memeforcehunt.app.gui.main;

import io.github.alttpj.memeforcehunt.app.gui.actions.StaticGuiActions;
import io.github.alttpj.memeforcehunt.app.gui.properties.SelectedFileProperty;
import io.github.alttpj.memeforcehunt.common.sprites.DefaultSpritemapWithSkins;
import io.github.alttpj.memeforcehunt.common.value.SpritemapWithSkin;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

@SuppressFBWarnings("EI_EXPOSE_REP")
public class DefaultSpriteTab extends HBox implements Initializable {

  private static final java.util.logging.Logger LOGGER = Logger.getLogger(DefaultSpriteTab.class.getCanonicalName());

  private final RandomGenerator random = RandomGeneratorFactory.getDefault().create();

  @FXML
  private ItemSkinList defaultSpritesItemSkinList;

  @FXML
  private Button patchButton;

  @FXML
  private Button randomButton;
  private final SelectedFileProperty selectedFileProperty = new SelectedFileProperty();

  @SuppressFBWarnings(value = "CT_CONSTRUCTOR_THROW", justification = "fxml")
  public DefaultSpriteTab() {
    // fmxl
    final FXMLLoader fxmlLoader =
        new FXMLLoader(getClass().getResource("/io/github/alttpj/memeforcehunt/app/gui/main/DefaultSprites.fxml"));
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
    DefaultSpritemapWithSkins.values().forEach(this.defaultSpritesItemSkinList::addSkin);
    this.selectedFileProperty.addListener((source, old, newValue) -> {
      if (newValue.isEmpty()) {
        getPatchButton().setDisable(true);
        getRandomButton().setDisable(true);
        return;
      }

      getPatchButton().setDisable(false);
      getRandomButton().setDisable(false);
    });
  }

  public ItemSkinList getDefaultSpritesItemSkinList() {
    return this.defaultSpritesItemSkinList;
  }

  public Button getPatchButton() {
    return this.patchButton;
  }

  public Button getRandomButton() {
    return this.randomButton;
  }

  @FXML
  public void doPatchRom(final ActionEvent clickAction) {
    final SpritemapWithSkin selectedItem = this.defaultSpritesItemSkinList.getSelectionModel().getSelectedItem();
    if (selectedItem == null) {
      final Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Unable to patch rom");
      alert.setHeaderText("No item selected");
      alert.setContentText("Please select an item and try again.");

      alert.showAndWait();
      return;

    }

    try {
      StaticGuiActions.patch(this.selectedFileProperty.get().orElseThrow(), selectedItem);
      final Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Rom file patched successfully.");
      alert.setHeaderText(selectedItem.getSpriteName());
      alert.setContentText("The skin [" + selectedItem.getSpriteName() + "] was successfully patched into your rom.");

      alert.showAndWait();

    } catch (final IOException ioException) {
      LOGGER.log(Level.SEVERE, ioException,
          () -> "Error patching [" + selectedItem + "] into [" + this.selectedFileProperty.get() + "].");
      final Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Unable to patch rom");
      alert.setHeaderText("Error patching supplied rom file.");
      alert.setContentText("Error message was: " + ioException.getMessage() + "\n"
          + "If the error persists, please start MemeforceHunt from the command line "
          + "and report the StackTrace on https://github.com/alttpj/MemeforceHunt/issues/.");

      alert.showAndWait();
    }
  }

  @FXML
  public void doPatchRandom(final ActionEvent actionEvent) {
    final ObservableList<SpritemapWithSkin> availableItems = getDefaultSpritesItemSkinList().getItems();
    final int max = availableItems.size();
    final int randomItemNumber = this.random.nextInt(max);
    final SpritemapWithSkin selectedItem = availableItems.get(randomItemNumber);

    try {
      StaticGuiActions.patch(this.selectedFileProperty.get().orElseThrow(), selectedItem);

      final Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Rom file patched successfully.");
      alert.setHeaderText("** top secret random skin **");
      alert.setContentText("A random skin was successfully patched into your rom.");

      alert.showAndWait();

    } catch (final IOException ioException) {
      LOGGER.log(Level.SEVERE, ioException,
          () -> "Error patching a random skin into [" + this.selectedFileProperty.get() + "].");
      final Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Unable to patch rom");
      alert.setHeaderText("Error patching supplied rom file.");
      alert.setContentText("Error message was: " + ioException.getMessage() + "\n"
          + "If the error persists, please start MemeforceHunt from the command line "
          + "and report the StackTrace on https://github.com/alttpj/MemeforceHunt/issues/.");

      alert.showAndWait();
    }
  }

  public SelectedFileProperty fileProperty() {
    return this.selectedFileProperty;
  }
}
