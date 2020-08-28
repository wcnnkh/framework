package scw.json.gson;

import java.util.Map;
import java.util.Set;

import scw.json.gson.internal.LinkedTreeMap;

/**
 * A class representing an object type in Json. An object consists of name-value pairs where names
 * are strings, and values are any other type of {@link GsonJsonElement}. This allows for a creating a
 * tree of JsonElements. The member elements of this object are maintained in order they were added.
 *
 * @author Inderjeet Singh
 * @author Joel Leitch
 */
public final class GsonJsonObject extends GsonJsonElement {
  private final LinkedTreeMap<String, GsonJsonElement> members =
      new LinkedTreeMap<String, GsonJsonElement>();

  /**
   * Creates a deep copy of this element and all its children
   */
  @Override
  public GsonJsonObject deepCopy() {
    GsonJsonObject result = new GsonJsonObject();
    for (Map.Entry<String, GsonJsonElement> entry : members.entrySet()) {
      result.add(entry.getKey(), entry.getValue().deepCopy());
    }
    return result;
  }

  /**
   * Adds a member, which is a name-value pair, to self. The name must be a String, but the value
   * can be an arbitrary JsonElement, thereby allowing you to build a full tree of JsonElements
   * rooted at this node.
   *
   * @param property name of the member.
   * @param value the member object.
   */
  public void add(String property, GsonJsonElement value) {
    if (value == null) {
      value = JsonNull.INSTANCE;
    }
    members.put(property, value);
  }

  /**
   * Removes the {@code property} from this {@link GsonJsonObject}.
   *
   * @param property name of the member that should be removed.
   * @return the {@link GsonJsonElement} object that is being removed.
   */
  public GsonJsonElement remove(String property) {
    return members.remove(property);
  }

  /**
   * Convenience method to add a primitive member. The specified value is converted to a
   * JsonPrimitive of String.
   *
   * @param property name of the member.
   * @param value the string value associated with the member.
   */
  public void addProperty(String property, String value) {
    add(property, createJsonElement(value));
  }

  /**
   * Convenience method to add a primitive member. The specified value is converted to a
   * JsonPrimitive of Number.
   *
   * @param property name of the member.
   * @param value the number value associated with the member.
   */
  public void addProperty(String property, Number value) {
    add(property, createJsonElement(value));
  }

  /**
   * Convenience method to add a boolean member. The specified value is converted to a
   * JsonPrimitive of Boolean.
   *
   * @param property name of the member.
   * @param value the number value associated with the member.
   */
  public void addProperty(String property, Boolean value) {
    add(property, createJsonElement(value));
  }

  /**
   * Convenience method to add a char member. The specified value is converted to a
   * JsonPrimitive of Character.
   *
   * @param property name of the member.
   * @param value the number value associated with the member.
   */
  public void addProperty(String property, Character value) {
    add(property, createJsonElement(value));
  }

  /**
   * Creates the proper {@link GsonJsonElement} object from the given {@code value} object.
   *
   * @param value the object to generate the {@link GsonJsonElement} for
   * @return a {@link JsonPrimitive} if the {@code value} is not null, otherwise a {@link JsonNull}
   */
  private GsonJsonElement createJsonElement(Object value) {
    return value == null ? JsonNull.INSTANCE : new JsonPrimitive(value);
  }

  /**
   * Returns a set of members of this object. The set is ordered, and the order is in which the
   * elements were added.
   *
   * @return a set of members of this object.
   */
  public Set<Map.Entry<String, GsonJsonElement>> entrySet() {
    return members.entrySet();
  }

  /**
   * Returns a set of members key values.
   *
   * @return a set of member keys as Strings
   */
  public Set<String> keySet() {
    return members.keySet();
  }

  /**
   * Returns the number of key/value pairs in the object.
   *
   * @return the number of key/value pairs in the object.
   */
  public int size() {
    return members.size();
  }

  /**
   * Convenience method to check if a member with the specified name is present in this object.
   *
   * @param memberName name of the member that is being checked for presence.
   * @return true if there is a member with the specified name, false otherwise.
   */
  public boolean has(String memberName) {
    return members.containsKey(memberName);
  }

  /**
   * Returns the member with the specified name.
   *
   * @param memberName name of the member that is being requested.
   * @return the member matching the name. Null if no such member exists.
   */
  public GsonJsonElement get(String memberName) {
    return members.get(memberName);
  }

  /**
   * Convenience method to get the specified member as a JsonPrimitive element.
   *
   * @param memberName name of the member being requested.
   * @return the JsonPrimitive corresponding to the specified member.
   */
  public JsonPrimitive getAsJsonPrimitive(String memberName) {
    return (JsonPrimitive) members.get(memberName);
  }

  /**
   * Convenience method to get the specified member as a JsonArray.
   *
   * @param memberName name of the member being requested.
   * @return the JsonArray corresponding to the specified member.
   */
  public GsonJsonArray getAsJsonArray(String memberName) {
    return (GsonJsonArray) members.get(memberName);
  }

  /**
   * Convenience method to get the specified member as a JsonObject.
   *
   * @param memberName name of the member being requested.
   * @return the JsonObject corresponding to the specified member.
   */
  public GsonJsonObject getAsJsonObject(String memberName) {
    return (GsonJsonObject) members.get(memberName);
  }

  @Override
  public boolean equals(Object o) {
    return (o == this) || (o instanceof GsonJsonObject
        && ((GsonJsonObject) o).members.equals(members));
  }

  @Override
  public int hashCode() {
    return members.hashCode();
  }
}
