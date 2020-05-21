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

package io.github.alttpj.memeforcehunt.app.gui.properties;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import java.io.File;
import java.util.Optional;

public final class SelectedFileProperty extends SimpleObjectProperty<Optional<File>> {
  public SelectedFileProperty() {
    super(Optional.empty());
  }

  public SelectedFileProperty(final File initialValue) {
    super(Optional.ofNullable(initialValue));
  }

  public void set(final File file) {
    set(Optional.ofNullable(file));
  }

  @Override
  public void addListener(final ChangeListener<? super Optional<File>> listener) {
    super.addListener(listener);
  }

}
