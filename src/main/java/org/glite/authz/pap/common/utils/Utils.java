package org.glite.authz.pap.common.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.configuration.INIConfiguration;
import org.glite.authz.pap.common.exceptions.PAPException;
import org.glite.authz.pap.distribution.DistributionConfiguration;
import org.glite.authz.pap.repository.dao.filesystem.FileSystemPapDAO;

/**
 * This class contains utility methods.
 */
public class Utils {

    /**
     * Returns a <code>String</code> filled with spaces.
     * 
     * @param n number of spaces to fill the string with.
     * @return a <code>String</code> composed by <i>n</i> spaces.
     */
    public static String fillWithSpaces(int n) {

        StringBuffer sb = new StringBuffer(n);

        for (int i = 0; i < n; i++) {
            sb.append(' ');
        }

        return sb.toString();
    }

    /**
     * Returns a set of strings. Each string is part of a key defined in an (INI) configuration.
     * This method is used in {@link DistributionConfiguration} and in {@link FileSystemPapDAO} to
     * retrieve the set of aliases of the paps defined in the configuration. The alias is retrieved
     * from a key defined as:<br>
     * <i>prefix</i>.<i>alias</i>[.<i>something_else</i>]
     * 
     * @param iniConfiguration the configuration where the keys are defined.
     * @param prefix the prefix of the keys to search for.
     * @return the set of aliases.
     */
    @SuppressWarnings("unchecked")
    public static Set<String> getAliasSet(INIConfiguration iniConfiguration, String prefix) {
        return getAliasSet((Iterator<String>) iniConfiguration.getKeys(prefix));
    }

    /**
     * See method {@link Utils#getAliasSet(INIConfiguration, String)}.
     * 
     * @param iterator an iterator on strings retrieved from a configuration using the method
     *            {@link INIConfiguration#getKeys(String)}.
     * @return the set of aliases.
     */
    public static Set<String> getAliasSet(Iterator<String> iterator) {

        Set<String> aliasSet = new HashSet<String>();

        while (iterator.hasNext()) {
            String key = iterator.next();

            int firstAliasChar = key.indexOf('.') + 1;
            int lastAliasChar = key.indexOf('.', firstAliasChar);

            String alias = key.substring(firstAliasChar, lastAliasChar);

            aliasSet.add(alias);
        }
        return aliasSet;
    }

    /**
     * Returns the index in this array of the first occurrence of the specified element, or -1 if
     * this array does not contain this element. More formally, returns the lowest index <tt>i</tt>
     * such that <tt>object.equals(array[i])</tt>, or -1 if there is no such index.
     * 
     * @param object element to search for.
     * @param array the <code>Object[]</code> to search in.
     * @return the index of the first occurrence of the specified element, or -1 if no such element
     *         were found or <code>object</code> is <code>null</code>.
     */
    public static int indexOf(Object object, Object[] array) {

        if (object == null) {
            return -1;
        }

        for (int i = 0; i < array.length; i++) {

            if (object.equals(array[i])) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Checks if a string is not null or not empty.
     * 
     * @param s the string to check.
     * @return <code>true</code> if the string is not <code>null</code> or not <code>empty</code>,
     *         <code>false</code> otherwise.
     */
    public static boolean isDefined(String s) {

        if (s == null) {
            return false;
        }

        if (s.length() == 0) {
            return false;
        }

        return true;
    }

    public static void touchURL(String url) {

        try {

            URL u = new URL(url);

            URLConnection conn = u.openConnection();

            // Ignore content...
            conn.getContent();

        } catch (MalformedURLException e) {

            throw new PAPException("Malformed URL passed as argument: " + e.getMessage(), e);

        } catch (IOException e) {

            throw new PAPException("Error opening URL connection: " + e.getMessage(), e);
        }
    }
}
