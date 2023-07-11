package fr.konoashi.proxyprovider.service.chat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class JsonUtils
{

    /**
     * Does the given JsonObject contain an array field with the given name?
     */
    public static boolean isJsonArray(JsonObject p_151202_0_, String p_151202_1_)
    {
        return !hasField(p_151202_0_, p_151202_1_) ? false : p_151202_0_.get(p_151202_1_).isJsonArray();
    }


    /**
     * Does the given JsonObject contain a field with the given name?
     */
    public static boolean hasField(JsonObject p_151204_0_, String p_151204_1_)
    {
        return p_151204_0_ == null ? false : p_151204_0_.get(p_151204_1_) != null;
    }

    /**
     * Gets the string value of the given JsonElement.  Expects the second parameter to be the name of the element's
     * field if an error message needs to be thrown.
     */
    public static String getString(JsonElement p_151206_0_, String p_151206_1_)
    {
        if (p_151206_0_.isJsonPrimitive())
        {
            return p_151206_0_.getAsString();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151206_1_ + " to be a string, was " + toString(p_151206_0_));
        }
    }

    /**
     * Gets the string value of the field on the JsonObject with the given name.
     */
    public static String getString(JsonObject p_151200_0_, String p_151200_1_)
    {
        if (p_151200_0_.has(p_151200_1_))
        {
            /**
             * Gets the string value of the given JsonElement.  Expects the second parameter to be the name of the
             * element's field if an error message needs to be thrown.
             */
            return getString(p_151200_0_.get(p_151200_1_), p_151200_1_);
        }
        else
        {
            throw new JsonSyntaxException("Missing " + p_151200_1_ + ", expected to find a string");
        }
    }


    /**
     * Gets the boolean value of the given JsonElement.  Expects the second parameter to be the name of the element's
     * field if an error message needs to be thrown.
     */
    public static boolean getBoolean(JsonElement p_151216_0_, String p_151216_1_)
    {
        if (p_151216_0_.isJsonPrimitive())
        {
            return p_151216_0_.getAsBoolean();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151216_1_ + " to be a Boolean, was " + toString(p_151216_0_));
        }
    }


    /**
     * Gets the boolean value of the field on the JsonObject with the given name, or the given default value if the
     * field is missing.
     */
    public static boolean getBoolean(JsonObject p_151209_0_, String p_151209_1_, boolean p_151209_2_)
    {
        return p_151209_0_.has(p_151209_1_) ? getBoolean(p_151209_0_.get(p_151209_1_), p_151209_1_) : p_151209_2_;
    }

    /**
     * Gets the float value of the given JsonElement.  Expects the second parameter to be the name of the element's
     * field if an error message needs to be thrown.
     */
    public static float getFloat(JsonElement p_151220_0_, String p_151220_1_)
    {
        if (p_151220_0_.isJsonPrimitive() && p_151220_0_.getAsJsonPrimitive().isNumber())
        {
            return p_151220_0_.getAsFloat();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151220_1_ + " to be a Float, was " + toString(p_151220_0_));
        }
    }


    /**
     * Gets the float value of the field on the JsonObject with the given name, or the given default value if the field
     * is missing.
     */
    public static float getFloat(JsonObject p_151221_0_, String p_151221_1_, float p_151221_2_)
    {
        return p_151221_0_.has(p_151221_1_) ? getFloat(p_151221_0_.get(p_151221_1_), p_151221_1_) : p_151221_2_;
    }

    /**
     * Gets the integer value of the given JsonElement.  Expects the second parameter to be the name of the element's
     * field if an error message needs to be thrown.
     */
    public static int getInt(JsonElement p_151215_0_, String p_151215_1_)
    {
        if (p_151215_0_.isJsonPrimitive() && p_151215_0_.getAsJsonPrimitive().isNumber())
        {
            return p_151215_0_.getAsInt();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151215_1_ + " to be a Int, was " + toString(p_151215_0_));
        }
    }

    /**
     * Gets the integer value of the field on the JsonObject with the given name.
     */
    public static int getInt(JsonObject p_151203_0_, String p_151203_1_)
    {
        if (p_151203_0_.has(p_151203_1_))
        {
            /**
             * Gets the integer value of the given JsonElement.  Expects the second parameter to be the name of the
             * element's field if an error message needs to be thrown.
             */
            return getInt(p_151203_0_.get(p_151203_1_), p_151203_1_);
        }
        else
        {
            throw new JsonSyntaxException("Missing " + p_151203_1_ + ", expected to find a Int");
        }
    }

    /**
     * Gets the integer value of the field on the JsonObject with the given name, or the given default value if the
     * field is missing.
     */
    public static int getInt(JsonObject p_151208_0_, String p_151208_1_, int p_151208_2_)
    {
        return p_151208_0_.has(p_151208_1_) ? getInt(p_151208_0_.get(p_151208_1_), p_151208_1_) : p_151208_2_;
    }

    /**
     * Gets the given JsonElement as a JsonObject.  Expects the second parameter to be the name of the element's field
     * if an error message needs to be thrown.
     */
    public static JsonObject getJsonObject(JsonElement p_151210_0_, String p_151210_1_)
    {
        if (p_151210_0_.isJsonObject())
        {
            return p_151210_0_.getAsJsonObject();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151210_1_ + " to be a JsonObject, was " + toString(p_151210_0_));
        }
    }


    /**
     * Gets the given JsonElement as a JsonArray.  Expects the second parameter to be the name of the element's field if
     * an error message needs to be thrown.
     */
    public static JsonArray getJsonArray(JsonElement p_151207_0_, String p_151207_1_)
    {
        if (p_151207_0_.isJsonArray())
        {
            return p_151207_0_.getAsJsonArray();
        }
        else
        {
            throw new JsonSyntaxException("Expected " + p_151207_1_ + " to be a JsonArray, was " + toString(p_151207_0_));
        }
    }

    /**
     * Gets the JsonArray field on the JsonObject with the given name.
     */
    public static JsonArray getJsonArray(JsonObject p_151214_0_, String p_151214_1_)
    {
        if (p_151214_0_.has(p_151214_1_))
        {
            /**
             * Gets the given JsonElement as a JsonArray.  Expects the second parameter to be the name of the element's
             * field if an error message needs to be thrown.
             */
            return getJsonArray(p_151214_0_.get(p_151214_1_), p_151214_1_);
        }
        else
        {
            throw new JsonSyntaxException("Missing " + p_151214_1_ + ", expected to find a JsonArray");
        }
    }


    /**
     * Gets a human-readable description of the given JsonElement's type.  For example: "a number (4)"
     */
    public static String toString(JsonElement p_151222_0_)
    {
        String s = org.apache.commons.lang3.StringUtils.abbreviateMiddle(String.valueOf((Object)p_151222_0_), "...", 10);

        if (p_151222_0_ == null)
        {
            return "null (missing)";
        }
        else if (p_151222_0_.isJsonNull())
        {
            return "null (json)";
        }
        else if (p_151222_0_.isJsonArray())
        {
            return "an array (" + s + ")";
        }
        else if (p_151222_0_.isJsonObject())
        {
            return "an object (" + s + ")";
        }
        else
        {
            if (p_151222_0_.isJsonPrimitive())
            {
                JsonPrimitive jsonprimitive = p_151222_0_.getAsJsonPrimitive();

                if (jsonprimitive.isNumber())
                {
                    return "a number (" + s + ")";
                }

                if (jsonprimitive.isBoolean())
                {
                    return "a boolean (" + s + ")";
                }
            }

            return s;
        }
    }
}
