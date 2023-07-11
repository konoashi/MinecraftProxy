package fr.konoashi.proxyprovider.service.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;


public class ChatStyle
{
    /** The parent of this ChatStyle.  Used for looking up values that this instance does not override. */
    private ChatStyle parentStyle;
    private EnumChatFormatting color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private String insertion;
    /** The base of the ChatStyle hierarchy.  All ChatStyle instances are implicitly children of this. */
    private static final ChatStyle rootStyle = new ChatStyle()
    {
        /**
         * Gets the effective color of this ChatStyle.
         */
        public EnumChatFormatting getColor()
        {
            return null;
        }
        /**
         * Whether or not text of this ChatStyle should be in bold.
         */
        public boolean getBold()
        {
            return false;
        }
        /**
         * Whether or not text of this ChatStyle should be italicized.
         */
        public boolean getItalic()
        {
            return false;
        }
        /**
         * Whether or not to format text of this ChatStyle using strikethrough.
         */
        public boolean getStrikethrough()
        {
            return false;
        }
        /**
         * Whether or not text of this ChatStyle should be underlined.
         */
        public boolean getUnderlined()
        {
            return false;
        }
        /**
         * Whether or not text of this ChatStyle should be obfuscated.
         */
        public boolean getObfuscated()
        {
            return false;
        }
        /**
         * Get the text to be inserted into Chat when the component is shift-clicked
         */
        public String getInsertion()
        {
            return null;
        }
        /**
         * Sets the color for this ChatStyle to the given value.  Only use color values for this; set other values using
         * the specific methods.
         */
        public ChatStyle setColor(EnumChatFormatting color)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets whether or not text of this ChatStyle should be in bold.  Set to false if, e.g., the parent style is
         * bold and you want text of this style to be unbolded.
         */
        public ChatStyle setBold(Boolean boldIn)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets whether or not text of this ChatStyle should be italicized.  Set to false if, e.g., the parent style is
         * italicized and you want to override that for this style.
         */
        public ChatStyle setItalic(Boolean italic)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets whether or not to format text of this ChatStyle using strikethrough.  Set to false if, e.g., the parent
         * style uses strikethrough and you want to override that for this style.
         */
        public ChatStyle setStrikethrough(Boolean strikethrough)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets whether or not text of this ChatStyle should be underlined.  Set to false if, e.g., the parent style is
         * underlined and you want to override that for this style.
         */
        public ChatStyle setUnderlined(Boolean underlined)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets whether or not text of this ChatStyle should be obfuscated.  Set to false if, e.g., the parent style is
         * obfuscated and you want to override that for this style.
         */
        public ChatStyle setObfuscated(Boolean obfuscated)
        {
            throw new UnsupportedOperationException();
        }
        /**
         * Sets the fallback ChatStyle to use if this ChatStyle does not override some value.  Without a parent, obvious
         * defaults are used (bold: false, underlined: false, etc).
         */
        public ChatStyle setParentStyle(ChatStyle parent)
        {
            throw new UnsupportedOperationException();
        }
        public String toString()
        {
            return "Style.ROOT";
        }
        /**
         * Creates a shallow copy of this style.  Changes to this instance's values will not be reflected in the copy,
         * but changes to the parent style's values WILL be reflected in both this instance and the copy, wherever
         * either does not override a value.
         */
        public ChatStyle createShallowCopy()
        {
            return this;
        }
        /**
         * Creates a deep copy of this style.  No changes to this instance or its parent style will be reflected in the
         * copy.
         */
        public ChatStyle createDeepCopy()
        {
            return this;
        }
        /**
         * Gets the equivalent text formatting code for this style, without the initial section sign (U+00A7) character.
         */
        public String getFormattingCode()
        {
            return "";
        }
    };

    /**
     * Gets the effective color of this ChatStyle.
     */
    public EnumChatFormatting getColor()
    {
        return this.color == null ? this.getParent().getColor() : this.color;
    }

    /**
     * Whether or not text of this ChatStyle should be in bold.
     */
    public boolean getBold()
    {
        return this.bold == null ? this.getParent().getBold() : this.bold.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be italicized.
     */
    public boolean getItalic()
    {
        return this.italic == null ? this.getParent().getItalic() : this.italic.booleanValue();
    }

    /**
     * Whether or not to format text of this ChatStyle using strikethrough.
     */
    public boolean getStrikethrough()
    {
        return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be underlined.
     */
    public boolean getUnderlined()
    {
        return this.underlined == null ? this.getParent().getUnderlined() : this.underlined.booleanValue();
    }

    /**
     * Whether or not text of this ChatStyle should be obfuscated.
     */
    public boolean getObfuscated()
    {
        return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated.booleanValue();
    }

    /**
     * Whether or not this style is empty (inherits everything from the parent).
     */
    public boolean isEmpty()
    {
        return this.bold == null && this.italic == null && this.strikethrough == null && this.underlined == null && this.obfuscated == null && this.color == null;
    }


    /**
     * Get the text to be inserted into Chat when the component is shift-clicked
     */
    public String getInsertion()
    {
        return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
    }

    /**
     * Sets the color for this ChatStyle to the given value.  Only use color values for this; set other values using the
     * specific methods.
     */
    public ChatStyle setColor(EnumChatFormatting color)
    {
        this.color = color;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be in bold.  Set to false if, e.g., the parent style is bold
     * and you want text of this style to be unbolded.
     */
    public ChatStyle setBold(Boolean boldIn)
    {
        this.bold = boldIn;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be italicized.  Set to false if, e.g., the parent style is
     * italicized and you want to override that for this style.
     */
    public ChatStyle setItalic(Boolean italic)
    {
        this.italic = italic;
        return this;
    }

    /**
     * Sets whether or not to format text of this ChatStyle using strikethrough.  Set to false if, e.g., the parent
     * style uses strikethrough and you want to override that for this style.
     */
    public ChatStyle setStrikethrough(Boolean strikethrough)
    {
        this.strikethrough = strikethrough;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be underlined.  Set to false if, e.g., the parent style is
     * underlined and you want to override that for this style.
     */
    public ChatStyle setUnderlined(Boolean underlined)
    {
        this.underlined = underlined;
        return this;
    }

    /**
     * Sets whether or not text of this ChatStyle should be obfuscated.  Set to false if, e.g., the parent style is
     * obfuscated and you want to override that for this style.
     */
    public ChatStyle setObfuscated(Boolean obfuscated)
    {
        this.obfuscated = obfuscated;
        return this;
    }

    /**
     * Set a text to be inserted into Chat when the component is shift-clicked
     */
    public ChatStyle setInsertion(String insertion)
    {
        this.insertion = insertion;
        return this;
    }

    /**
     * Sets the fallback ChatStyle to use if this ChatStyle does not override some value.  Without a parent, obvious
     * defaults are used (bold: false, underlined: false, etc).
     */
    public ChatStyle setParentStyle(ChatStyle parent)
    {
        this.parentStyle = parent;
        return this;
    }

    /**
     * Gets the equivalent text formatting code for this style, without the initial section sign (U+00A7) character.
     */
    public String getFormattingCode()
    {
        if (this.isEmpty())
        {
            return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
        }
        else
        {
            StringBuilder stringbuilder = new StringBuilder();

            if (this.getColor() != null)
            {
                stringbuilder.append((Object)this.getColor());
            }

            if (this.getBold())
            {
                stringbuilder.append((Object)EnumChatFormatting.BOLD);
            }

            if (this.getItalic())
            {
                stringbuilder.append((Object)EnumChatFormatting.ITALIC);
            }

            if (this.getUnderlined())
            {
                stringbuilder.append((Object)EnumChatFormatting.UNDERLINE);
            }

            if (this.getObfuscated())
            {
                stringbuilder.append((Object)EnumChatFormatting.OBFUSCATED);
            }

            if (this.getStrikethrough())
            {
                stringbuilder.append((Object)EnumChatFormatting.STRIKETHROUGH);
            }

            return stringbuilder.toString();
        }
    }

    /**
     * Gets the immediate parent of this ChatStyle.
     */
    private ChatStyle getParent()
    {
        return this.parentStyle == null ? rootStyle : this.parentStyle;
    }

    public String toString()
    {
        return "Style{hasParent=" + (this.parentStyle != null) + ", color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", obfuscated=" + this.obfuscated + ", insertion=" + this.getInsertion() + '}';
    }

    public boolean equals(Object p_equals_1_)
    {
        if (this == p_equals_1_)
        {
            return true;
        }
        else if (!(p_equals_1_ instanceof ChatStyle))
        {
            return false;
        }
        else
        {
            boolean flag;
            label0:
            {
                ChatStyle chatstyle = (ChatStyle)p_equals_1_;

                if (this.getBold() == chatstyle.getBold() && this.getColor() == chatstyle.getColor() && this.getItalic() == chatstyle.getItalic() && this.getObfuscated() == chatstyle.getObfuscated() && this.getStrikethrough() == chatstyle.getStrikethrough() && this.getUnderlined() == chatstyle.getUnderlined())
                {
                    label85:
                    {

                        if (this.getInsertion() != null)
                        {
                            if (this.getInsertion().equals(chatstyle.getInsertion()))
                            {
                                break label0;
                            }
                        }
                        else if (chatstyle.getInsertion() == null)
                        {
                            break label0;
                        }
                    }
                }

                flag = false;
                return flag;
            }
            flag = true;
            return flag;
        }
    }

    public int hashCode()
    {
        int i = this.color.hashCode();
        i = 31 * i + this.bold.hashCode();
        i = 31 * i + this.italic.hashCode();
        i = 31 * i + this.underlined.hashCode();
        i = 31 * i + this.strikethrough.hashCode();
        i = 31 * i + this.obfuscated.hashCode();
        i = 31 * i + this.insertion.hashCode();
        return i;
    }

    /**
     * Creates a shallow copy of this style.  Changes to this instance's values will not be reflected in the copy, but
     * changes to the parent style's values WILL be reflected in both this instance and the copy, wherever either does
     * not override a value.
     */
    public ChatStyle createShallowCopy()
    {
        ChatStyle chatstyle = new ChatStyle();
        chatstyle.bold = this.bold;
        chatstyle.italic = this.italic;
        chatstyle.strikethrough = this.strikethrough;
        chatstyle.underlined = this.underlined;
        chatstyle.obfuscated = this.obfuscated;
        chatstyle.color = this.color;
        chatstyle.parentStyle = this.parentStyle;
        chatstyle.insertion = this.insertion;
        return chatstyle;
    }

    /**
     * Creates a deep copy of this style.  No changes to this instance or its parent style will be reflected in the
     * copy.
     */
    public ChatStyle createDeepCopy()
    {
        ChatStyle chatstyle = new ChatStyle();
        chatstyle.setBold(Boolean.valueOf(this.getBold()));
        chatstyle.setItalic(Boolean.valueOf(this.getItalic()));
        chatstyle.setStrikethrough(Boolean.valueOf(this.getStrikethrough()));
        chatstyle.setUnderlined(Boolean.valueOf(this.getUnderlined()));
        chatstyle.setObfuscated(Boolean.valueOf(this.getObfuscated()));
        chatstyle.setColor(this.getColor());
        chatstyle.setInsertion(this.getInsertion());
        return chatstyle;
    }

    public static class Serializer implements JsonDeserializer<ChatStyle>, JsonSerializer<ChatStyle>
    {
        public ChatStyle deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException
        {
            if (p_deserialize_1_.isJsonObject())
            {
                ChatStyle chatstyle = new ChatStyle();
                JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();

                if (jsonobject == null)
                {
                    return null;
                }
                else
                {
                    if (jsonobject.has("bold"))
                    {
                        chatstyle.bold = Boolean.valueOf(jsonobject.get("bold").getAsBoolean());
                    }

                    if (jsonobject.has("italic"))
                    {
                        chatstyle.italic = Boolean.valueOf(jsonobject.get("italic").getAsBoolean());
                    }

                    if (jsonobject.has("underlined"))
                    {
                        chatstyle.underlined = Boolean.valueOf(jsonobject.get("underlined").getAsBoolean());
                    }

                    if (jsonobject.has("strikethrough"))
                    {
                        chatstyle.strikethrough = Boolean.valueOf(jsonobject.get("strikethrough").getAsBoolean());
                    }

                    if (jsonobject.has("obfuscated"))
                    {
                        chatstyle.obfuscated = Boolean.valueOf(jsonobject.get("obfuscated").getAsBoolean());
                    }

                    if (jsonobject.has("color"))
                    {
                        chatstyle.color = (EnumChatFormatting)p_deserialize_3_.deserialize(jsonobject.get("color"), EnumChatFormatting.class);
                    }

                    if (jsonobject.has("insertion"))
                    {
                        chatstyle.insertion = jsonobject.get("insertion").getAsString();
                    }


                    return chatstyle;
                }
            }
            else
            {
                return null;
            }
        }

        public JsonElement serialize(ChatStyle p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_)
        {
            if (p_serialize_1_.isEmpty())
            {
                return null;
            }
            else
            {
                JsonObject jsonobject = new JsonObject();

                if (p_serialize_1_.bold != null)
                {
                    jsonobject.addProperty("bold", p_serialize_1_.bold);
                }

                if (p_serialize_1_.italic != null)
                {
                    jsonobject.addProperty("italic", p_serialize_1_.italic);
                }

                if (p_serialize_1_.underlined != null)
                {
                    jsonobject.addProperty("underlined", p_serialize_1_.underlined);
                }

                if (p_serialize_1_.strikethrough != null)
                {
                    jsonobject.addProperty("strikethrough", p_serialize_1_.strikethrough);
                }

                if (p_serialize_1_.obfuscated != null)
                {
                    jsonobject.addProperty("obfuscated", p_serialize_1_.obfuscated);
                }

                if (p_serialize_1_.color != null)
                {
                    jsonobject.add("color", p_serialize_3_.serialize(p_serialize_1_.color));
                }

                if (p_serialize_1_.insertion != null)
                {
                    jsonobject.add("insertion", p_serialize_3_.serialize(p_serialize_1_.insertion));
                }


                return jsonobject;
            }
        }
    }
}
