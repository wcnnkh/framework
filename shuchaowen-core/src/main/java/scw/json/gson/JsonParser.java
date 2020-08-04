/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scw.json.gson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import scw.json.gson.internal.Streams;
import scw.json.gson.stream.JsonReader;
import scw.json.gson.stream.JsonToken;
import scw.json.gson.stream.MalformedJsonException;

/**
 * A parser to parse Json into a parse tree of {@link GsonJsonElement}s
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public final class JsonParser {

  /**
   * Parses the specified JSON string into a parse tree
   *
   * @param json JSON text
   * @return a parse tree of {@link GsonJsonElement}s corresponding to the specified JSON
   * @throws JsonParseException if the specified text is not valid JSON
   */
  public GsonJsonElement parse(String json) throws JsonSyntaxException {
    return parse(new StringReader(json));
  }

  /**
   * Parses the specified JSON string into a parse tree
   *
   * @param json JSON text
   * @return a parse tree of {@link GsonJsonElement}s corresponding to the specified JSON
   * @throws JsonParseException if the specified text is not valid JSON
   */
  public GsonJsonElement parse(Reader json) throws JsonIOException, JsonSyntaxException {
    try {
      JsonReader jsonReader = new JsonReader(json);
      GsonJsonElement element = parse(jsonReader);
      if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
        throw new JsonSyntaxException("Did not consume the entire document.");
      }
      return element;
    } catch (MalformedJsonException e) {
      throw new JsonSyntaxException(e);
    } catch (IOException e) {
      throw new JsonIOException(e);
    } catch (NumberFormatException e) {
      throw new JsonSyntaxException(e);
    }
  }

  /**
   * Returns the next value from the JSON stream as a parse tree.
   *
   * @throws JsonParseException if there is an IOException or if the specified
   *     text is not valid JSON
   * @since 1.6
   */
  public GsonJsonElement parse(JsonReader json) throws JsonIOException, JsonSyntaxException {
    boolean lenient = json.isLenient();
    json.setLenient(true);
    try {
      return Streams.parse(json);
    } catch (StackOverflowError e) {
      throw new JsonParseException("Failed parsing JSON source: " + json + " to Json", e);
    } catch (OutOfMemoryError e) {
      throw new JsonParseException("Failed parsing JSON source: " + json + " to Json", e);
    } finally {
      json.setLenient(lenient);
    }
  }
}
