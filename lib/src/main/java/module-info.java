module memforcehunt.lib {
  requires memeforcehunt.common.value;
  requires alttpj.library;

  requires static org.immutables.value.annotations;
  requires static com.github.spotbugs.annotations;

  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.yaml;

  opens io.github.alttpj.memeforcehunt.lib to com.fasterxml.jackson.databind;
  opens io.github.alttpj.memeforcehunt.lib.impl to com.fasterxml.jackson.databind;

  exports io.github.alttpj.memeforcehunt.lib;
}
