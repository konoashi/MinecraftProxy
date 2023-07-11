package fr.konoashi.proxyprovider.service.chat;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.regex.Pattern;

public class StringTranslate
{
    /** Pattern that matches numeric variable placeholders in a resource string, such as "%d", "%3$d", "%.2f" */
    private static final Pattern numericVariablePattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    /** A Splitter that splits a string on the first "=".  For example, "a=b=c" would split into ["a", "b=c"]. */
    private static final Splitter equalSignSplitter = Splitter.on('=').limit(2);
    /** Is the private singleton instance of StringTranslate. */
    private static StringTranslate instance = new StringTranslate();
    private final Map<String, String> languageList = Maps.<String, String>newHashMap();
    /** The time, in milliseconds since epoch, that this instance was last updated */
    private long lastUpdateTimeInMilliseconds;

    public StringTranslate()
    {
        InputStream inputstream = StringTranslate.class.getResourceAsStream("/assets/minecraft/lang/en_US.lang");
        inject(this, inputstream);
    }

    public static void inject(InputStream inputstream)
    {
        inject(instance, inputstream);
    }

    private static void inject(StringTranslate inst, InputStream inputstream)
    {
        java.util.HashMap<String, String> map = parseLangFile(inputstream);
        inst.languageList.putAll(map);
        inst.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
    }

    public static java.util.HashMap<String,String> parseLangFile(InputStream inputstream)
    {
        return Maps.newHashMap();

    }

    /**
     * Return the StringTranslate singleton instance
     */
    static StringTranslate getInstance()
    {
        /** Is the private singleton instance of StringTranslate. */
        return instance;
    }


    /**
     * Replaces all the current instance's translations with the ones that are passed in.
     */
    public static synchronized void replaceWith(Map<String, String> p_135063_0_)
    {
        instance.languageList.clear();
        instance.languageList.putAll(p_135063_0_);
        instance.lastUpdateTimeInMilliseconds = System.currentTimeMillis();
    }

    /**
     * Translate a key to current language.
     */
    public synchronized String translateKey(String key)
    {
        return this.tryTranslateKey(key);
    }

    /**
     * Translate a key to current language applying String.format()
     */
    public synchronized String translateKeyFormat(String key, Object... format)
    {
        String s = this.tryTranslateKey(key);

        try
        {
            return String.format(s, format);
        }
        catch (IllegalFormatException var5)
        {
            return "Format error: " + s;
        }
    }

    /**
     * Tries to look up a translation for the given key; spits back the key if no result was found.
     */
    private String tryTranslateKey(String key)
    {
        String s = (String)this.languageList.get(key);
        return s == null ? key : s;
    }

    /**
     * Returns true if the passed key is in the translation table.
     */
    public synchronized boolean isKeyTranslated(String key)
    {
        return this.languageList.containsKey(key);
    }

    /**
     * Gets the time, in milliseconds since epoch, that this instance was last updated
     */
    public long getLastUpdateTimeInMilliseconds()
    {
        return this.lastUpdateTimeInMilliseconds;
    }
}