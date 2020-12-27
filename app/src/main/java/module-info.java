module memeforcehunt.app {
  requires javafx.fxml;
  requires java.logging;
  requires javafx.controls;
  requires javafx.graphics;
  requires javafx.swing;
  requires java.desktop;

  requires info.picocli;
  requires memeforcehunt.common.sprites;
  requires memeforcehunt.common.value;
  requires memforcehunt.lib;
  requires org.jfxtras.styles.jmetro;
  requires alttpj.library;

  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.yaml;

  requires static com.github.spotbugs.annotations;


  // fxml needs to modify the gui classes.
  opens io.github.alttpj.memeforcehunt.app.gui to javafx.fxml;
  opens io.github.alttpj.memeforcehunt.app.gui.main to javafx.fxml;
  opens io.github.alttpj.memeforcehunt.app.gui.preferences to javafx.fxml;
  opens io.github.alttpj.memeforcehunt.app.gui.editor to javafx.fxml;

  opens io.github.alttpj.memeforcehunt.app.cli.commands to info.picocli;

  opens io.github.alttpj.memeforcehunt.app.config to com.fasterxml.jackson.databind;


  // those classes are accessible from outside, e.g. for the JavaFX loader.
  exports io.github.alttpj.memeforcehunt.app.cli;
  exports io.github.alttpj.memeforcehunt.app.cli.commands;
  exports io.github.alttpj.memeforcehunt.app.gui;
  exports io.github.alttpj.memeforcehunt.app.gui.editor;
}
