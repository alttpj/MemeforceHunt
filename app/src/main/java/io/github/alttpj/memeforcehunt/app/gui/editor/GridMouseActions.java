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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;

import java.util.logging.Logger;

public class GridMouseActions {

  private static final Logger LOG = Logger.getLogger(GridMouseActions.class.getCanonicalName());

  private final boolean hoverEffect;

  private final ObjectProperty<ColorSelectorCell> selectedColorSelectorCell = new SimpleObjectProperty<>();

  public GridMouseActions(final ObjectProperty<ColorSelectorCell> selectedColorSelectorCell) {
    this.hoverEffect = true;
    this.selectedColorSelectorCell.bind(selectedColorSelectorCell);
  }

  public void makePaintable(final Node node) {
    // that's all there is needed for hovering, the other code is just for painting
    if (this.hoverEffect) {
      node.hoverProperty().addListener((observable, oldValue, newValue) -> {
        //LOG.log(Level.INFO, observable + ": " + newValue);

        if (newValue) {
          ((ColourableCell) node).hoverHighlight();
        } else {
          ((ColourableCell) node).hoverUnhighlight();
        }

        for (final String styleClass : node.getStyleClass()) {
          //LOG.log(Level.INFO, node + ": " + styleClass);
        }
      });
    }

    node.setOnMousePressed(MouseActionHolder.mousePressedEventHandler(this.selectedColorSelectorCell));
    node.setOnDragDetected(MouseActionHolder.dragDetectedEventHandler(this.selectedColorSelectorCell));
    node.setOnMouseDragEntered(MouseActionHolder.mouseDragEnteredEventHandler(this.selectedColorSelectorCell));
  }

  static class MouseActionHolder {

    static EventHandler<MouseEvent> mousePressedEventHandler(final ObjectProperty<ColorSelectorCell> colorSelectorCell) {
      return event -> {
        final ColourableCell cell = (ColourableCell) event.getSource();

        if (event.isPrimaryButtonDown()) {
          cell.paint(colorSelectorCell.get());
        }
      };
    }

    static EventHandler<MouseEvent> mouseDraggedEventHandler(final ObjectProperty<ColorSelectorCell> colorSelectorCell) {
      return event -> {
        final PickResult pickResult = event.getPickResult();
        final Node node = pickResult.getIntersectedNode();

        if (node instanceof ColourableCell) {
          final ColourableCell cell = (ColourableCell) node;

          if (event.isPrimaryButtonDown()) {
            cell.paint(colorSelectorCell.get());
          }
        }
      };
    }

    static EventHandler<MouseEvent> mouseReleasedEventHandler(final ObjectProperty<ColorSelectorCell> colorSelectorCell) {
      return event -> {
        // noop
      };
    }


    static EventHandler<MouseEvent> dragDetectedEventHandler(final ObjectProperty<ColorSelectorCell> colorSelectorCell) {
      return event -> {
        final ColourableCell cell = (ColourableCell) event.getSource();
        cell.startFullDrag();
      };
    }

    static EventHandler<MouseEvent> mouseDragEnteredEventHandler(final ObjectProperty<ColorSelectorCell> colorSelectorCell) {
      return event -> {
        final ColourableCell cell = (ColourableCell) event.getSource();

        if (event.isPrimaryButtonDown()) {
          cell.paint(colorSelectorCell.get());
        }
      };
    }
  }
}
