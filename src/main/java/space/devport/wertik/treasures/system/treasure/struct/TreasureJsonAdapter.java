package space.devport.wertik.treasures.system.treasure.struct;

import space.devport.dock.lib.google.gson.*;
import space.devport.dock.lib.google.gson.reflect.TypeToken;
import space.devport.wertik.treasures.system.struct.TreasureData;

import java.lang.reflect.Type;
import java.util.UUID;

public class TreasureJsonAdapter implements JsonSerializer<Treasure>, JsonDeserializer<Treasure> {

    @Override
    public JsonElement serialize(Treasure treasure, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        json.add("uniqueID", context.serialize(treasure.getUniqueID()));
        json.add("jsonLocation", context.serialize(treasure.getJsonLocation()));
        json.add("toolName", context.serialize(treasure.getToolName()));
        json.addProperty("treasureData", treasure.getTreasureData().getAsString());
        json.addProperty("found", treasure.isFound());
        json.addProperty("hello", "world");

        return json;
    }

    @Override
    public Treasure deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        if (!jsonElement.isJsonObject())
            return null;

        JsonObject json = jsonElement.getAsJsonObject();

        UUID uniqueID = context.deserialize(json.get("uniqueID"), new TypeToken<UUID>() {
        }.getType());

        Treasure treasure = new Treasure(uniqueID);

        JsonLocation jsonLocation = context.deserialize(json.get("jsonLocation"), new TypeToken<JsonLocation>() {
        }.getType());
        treasure.setJsonLocation(jsonLocation);

        String toolName = json.get("toolName").getAsString();
        treasure.setToolName(toolName);

        if (json.has("treasureData")) {
            String dataString = json.get("treasureData").getAsString();
            TreasureData data = TreasureData.fromString(dataString);
            treasure.setTreasureData(data);
        }

        treasure.setFound(json.get("found").getAsBoolean());
        return treasure;
    }
}
