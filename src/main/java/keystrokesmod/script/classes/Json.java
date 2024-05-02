package keystrokesmod.script.classes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Json {
    private JsonObject jsonObject;

    public Json(String jsonString) {
        this.jsonObject = (new JsonParser()).parse(jsonString).getAsJsonObject();
    }

    protected Json(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Json)) {
            return false;
        }
        return this.jsonObject.equals(((Json) object).jsonObject);
    }

    public boolean exists() {
        return jsonObject != null;
    }

    public String string() {
        return this.jsonObject.getAsString();
    }

    public String get(String key) {
        return this.jsonObject.get(key).getAsString();
    }

    public String get(String key, String defaultValue) {
        try {
            return this.jsonObject.get(key).getAsString();
        }
        catch (NullPointerException e) {
            return defaultValue;
        }
    }

    public Json object(String name) {
        return new Json(this.jsonObject.get(name).getAsJsonObject());
    }

    public List<Json> array(String name) {
        List<Json> list = new ArrayList<>();
        for (JsonElement element : this.jsonObject.getAsJsonArray(name)) {
            list.add(new Json(element.getAsJsonObject()));
        }
        return list;
    }

    public Map<String, Json> map() {
        Map<String, Json> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : this.jsonObject.entrySet()) {
            map.put(entry.getKey(), new Json(entry.getValue().getAsJsonObject()));
        }
        return map;
    }
}
