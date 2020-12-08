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

import io.github.alttpj.memeforcehunt.common.value.ULID;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class ULIDSerializer extends StdSerializer<ULID.Value> {
  private static final long serialVersionUID = -308017702725335081L;

  protected ULIDSerializer() {
    super(ULID.Value.class);
  }

  @Override
  public void serialize(final ULID.Value value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
    gen.writeString(value.toString());
  }
}
