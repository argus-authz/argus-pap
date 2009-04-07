package org.glite.authz.pap.common.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.glite.authz.pap.common.exceptions.PAPException;

public class Utils {

    public static String fillWithSpaces(int n) {

        StringBuffer sb = new StringBuffer(n);

        for (int i = 0; i < n; i++) {
            sb.append(' ');
        }

        return sb.toString();
    }

    public static boolean isDefined(String s) {

        if (s == null)
            return false;

        if (s.length() == 0)
            return false;

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
}
