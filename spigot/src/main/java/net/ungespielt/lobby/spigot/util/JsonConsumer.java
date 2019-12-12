package net.ungespielt.lobby.spigot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;

import java.util.function.Consumer;

/**
 * A Consumer that consumes a {@link JSONObject} and transforms it into a deserialized model.
 *
 * @author Felix Klauke <info@felix-klauke.de>
 */
public abstract class JsonConsumer<ModelType> implements Consumer<JSONObject> {

    /**
     * The gson instance.
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * The class of model to deserialize.
     */
    private final Class<? extends ModelType> jsonClazz;

    /**
     * If empty json objects are allowed.
     */
    private final boolean allowEmptyJsonObject;

    /**
     * Create a new json consumer.
     *
     * @param jsonClazz The json clazz.
     */
    public JsonConsumer(Class<? extends ModelType> jsonClazz) {
        this(jsonClazz, true);
    }

    /**
     * Create a new json consumer.
     *
     * @param jsonClazz            The json clazz.
     * @param allowEmptyJsonObject If empty json objects are allowed.
     */
    public JsonConsumer(Class<? extends ModelType> jsonClazz, boolean allowEmptyJsonObject) {
        this.jsonClazz = jsonClazz;
        this.allowEmptyJsonObject = allowEmptyJsonObject;
    }

    @Override
    public void accept(JSONObject jsonObject) {
        if (!allowEmptyJsonObject && jsonObject.length() == 0) {
            onFailure(jsonObject, new IllegalStateException("The json object should not have been empty."));
            return;
        }

        String jsonString = jsonObject.toString();

        try {
            ModelType json = GSON.fromJson(jsonString, jsonClazz);
            onSuccess(jsonObject, json);
        } catch (Exception e) {
            onFailure(jsonObject, e);
        }
    }

    /**
     * Report a failure in the json processing.
     *
     * @param jsonObject The json object.
     * @param throwable  The throwable.
     */
    public void onFailure(JSONObject jsonObject, Throwable throwable) {
        throw new UnsupportedOperationException("The json object was invalid but onFailure was not implemented.");
    }

    /**
     * The callback called when the deserialization process was successful.
     *
     * @param jsonObject The json object.
     * @param model The model.
     */
    public abstract void onSuccess(JSONObject jsonObject, ModelType model);
}
