package org.glite.authz.pap.encoder;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.lang.String;

public class PolicyFileEncoder {
    BWParser parser;

    public PolicyFileEncoder() {
        parser = null;
    }

    private void init(InputStream stream) {
        if (parser != null) { 
            parser.ReInit(stream);
        }
        else {
            parser = new BWParser(stream);
        }
    }

    private String doParse() throws EncodingException {
        try {
            String s = parser.Text();

            return s;
        }
        catch (ParseException e) {
            throw new EncodingException(e);
        }
    }

    public String parse(InputStream stream) throws EncodingException {
        init(stream);
        return doParse();
    }

    public String parse(String text) throws EncodingException {
        init(new ByteArrayInputStream(text.getBytes()));
        return doParse();
    }

    public String parse(File file) throws EncodingException {
        try {
            init(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new EncodingException(e);
        }
        return doParse();
    }

    public static void main(String[] args) {
        PolicyFileEncoder encoder = new PolicyFileEncoder();

        try {
            if (args.length > 0) {
                int i = 0;
                while (i < args.length) {
                    File f = new File(args[i++]);
                    System.out.println(encoder.parse(f));
                }
            }
            else
                System.out.println(encoder.parse(System.in));
        }
        catch (EncodingException e) {
            System.out.println(e.toString());
        }
    }
}
