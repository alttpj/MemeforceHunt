module memeforcehunt.common.sprites {
  requires java.base;
  requires java.desktop;
  requires java.logging;

  requires memeforcehunt.common.value;

  requires static org.immutables.value;

  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.yaml;

  opens io.github.alttpj.memeforcehunt.common.sprites.impl to com.fasterxml.jackson.databind;

  exports io.github.alttpj.memeforcehunt.common.sprites;
}
