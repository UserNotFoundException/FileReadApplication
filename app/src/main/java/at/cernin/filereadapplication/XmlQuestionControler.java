package at.cernin.filereadapplication;

import android.content.Context;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Walter on 16.05.2015.
 *
 * Verwaltet die Vorgaben aus dem Control-File und
 * liefert die Parameter zum Anzeigen und zur
 * Verwaltung eines Fragendialogs
 */
public class XmlQuestionControler {

    /**
     * Array mit allen Fragen und den dazu gehörenden Antworten
     */
    public final ArrayList<Question> questions;

    /**
     * Hilfsvariable für den XML-Parser, die einen nicht vorhandenen
     * Namespace repräsentiert.
     */
    private static final String ns = null;

    XmlQuestionControler(Context context, int resourceId) {
        // Einlesen einer JSON-Datei aus den Resourcen

        ArrayList<Question> result = null;
        InputStream in = context.getResources().openRawResource(resourceId);
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            result = readFeed( parser );
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) { }
        }
        questions = result;
    }

    private ArrayList<Question> readFeed( XmlPullParser parser ) throws IOException, XmlPullParserException {
        ArrayList<Question> questions = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "fragen");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("frage")) {
                questions.add(readQuestion(parser));
            }
            else {
                skip(parser);
            }
        }
        return questions;
    }

    public static class Question {
        public final String titel;
        public final int count;
        public final String questionFile;
        public final int questionTyp;
        public final ArrayList<Answer> answerFiles;

        private Question( String titel, int count, String questionFile,
                          int questionTyp, ArrayList<Answer> answerFiles
        ) {
            this.titel = titel;
            this.count = count;
            this.questionFile = questionFile;
            this.questionTyp = questionTyp;
            this.answerFiles = answerFiles;
        }
    }

    private Question readQuestion( XmlPullParser parser ) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "frage");
        String titel = null;
        int count = 0;
        String questionFile = null;
        int questionTyp = 0;
        ArrayList<Answer> answerFiles = new ArrayList( 4 );
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("titel")) {
                titel = readTitel(parser);
            }
            else if (name.equals("nummer")) {
                count = readCount(parser);
            }
            else if (name.equals("datei")) {
                questionFile = readFile(parser);
            }
            else if (name.equals("type")) {
                questionTyp = readQuestionTyp(parser);
            }
            else if (name.equals(("antworten"))){
                answerFiles = readQuestionAnswers(parser);
            }
            else {
                skip(parser);
            }
        }
        return new Question(titel, count, questionFile, questionTyp, answerFiles);
    }

    private String readTitel( XmlPullParser parser ) throws IOException, XmlPullParserException {
        parser.require( XmlPullParser.START_TAG, ns, "titel");
        String titel  = readText( parser );
        parser.require(XmlPullParser.END_TAG, ns, "titel");
        return titel;
    }

    private int readCount( XmlPullParser parser ) throws IOException, XmlPullParserException {
        parser.require( XmlPullParser.START_TAG, ns, "nummer");
        int count  = readInt(parser);
        parser.require(XmlPullParser.END_TAG, ns, "nummer");
        return count;
    }

    private String readFile( XmlPullParser parser ) throws IOException, XmlPullParserException {
        parser.require( XmlPullParser.START_TAG, ns, "datei");
        String file  = readText( parser );
        parser.require(XmlPullParser.END_TAG, ns, "datei");
        return file;
    }

    private int readQuestionTyp( XmlPullParser parser ) throws IOException, XmlPullParserException {
        parser.require( XmlPullParser.START_TAG, ns, "type");
        int questionTyp  = readInt(parser);
        parser.require(XmlPullParser.END_TAG, ns, "type");
        return questionTyp;
    }

    private ArrayList<Answer> readQuestionAnswers( XmlPullParser parser ) throws IOException, XmlPullParserException {
        ArrayList<Answer> answers = new ArrayList( 5 );

        parser.require(XmlPullParser.START_TAG, ns, "antworten");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("antwort")) {
                answers.add(readAnswer(parser));
            }
            else {
                skip(parser);
            }
        }
        return answers;
    }

    public static class Answer {
        public final String answerFile;
        public final boolean answerTrue;


        private Answer( String answerFile, boolean answerTrue ) {
            this.answerFile = answerFile;
            this.answerTrue = answerTrue;
        }
    }

    private Answer readAnswer( XmlPullParser parser ) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "antwort");
        String file = null;
        boolean answerTrue = false;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("datei")) {
                file = readFile(parser);
            }
            else if (name.equals("richtig")) {
                answerTrue = readAnswerTrue(parser);
            }
            else {
                skip(parser);
            }
        }
        return new Answer( file, answerTrue );
    }

     private boolean readAnswerTrue( XmlPullParser parser ) throws IOException, XmlPullParserException {
        parser.require( XmlPullParser.START_TAG, ns, "richtig");
        boolean answerTrue  = readBoolean(parser);
        parser.require(XmlPullParser.END_TAG, ns, "richtig");
        return answerTrue;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private int readInt(XmlPullParser parser) throws IOException, XmlPullParserException {
        int result = 0;
        if (parser.next() == XmlPullParser.TEXT) {
            String s = parser.getText();
            result = Integer.parseInt(s.trim());
            parser.nextTag();
        }
        return result;
    }

    private boolean readBoolean(XmlPullParser parser) throws IOException, XmlPullParserException {
        boolean result = false;
        if (parser.next() == XmlPullParser.TEXT) {
            int i = Integer.parseInt(parser.getText().trim());
            result = i != 0;
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException, IllegalStateException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
