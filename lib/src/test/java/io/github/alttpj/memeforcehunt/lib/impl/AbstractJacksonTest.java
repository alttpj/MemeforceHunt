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

package io.github.alttpj.memeforcehunt.lib.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.io.StringWriter;

public abstract class AbstractJacksonTest {

  private final ObjectMapper objectMapper = YamlProvider.getObjectMapper();
  private DeserializationContext context;
  private JsonParser parser;
  private final StringWriter stringWriter = new StringWriter();
  private JsonGenerator generator;

  @BeforeEach
  public void before() throws IOException {
    this.context = this.objectMapper.getDeserializationContext();
    this.parser = mock(JsonParser.class);
    when(this.parser.getParsingContext()).then(args -> this.context);
    this.generator = this.objectMapper.createGenerator(this.stringWriter);
  }

  protected DeserializationContext getContext() {
    return this.context;
  }

  protected JsonParser getParser() {
    return this.parser;
  }

  protected JsonGenerator getGenerator() {
    return this.generator;
  }

  protected String getWrittenString() {
    return this.stringWriter.toString();
  }
}
