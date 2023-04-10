package edu.cmu.webgen.project;

import com.github.jknack.handlebars.internal.text.StringEscapeUtils;
import edu.cmu.webgen.WebGen;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents formatted text documents, in which text is structured in to paragraphs
 * with formatted text.
 */
public class FormattedTextDocument extends AbstractContent {
    private final List<Paragraph> paragraphs;
    private final Metadata metadata;
    private final long textSize;

    public FormattedTextDocument(List<Paragraph> paragraphs, Metadata metadata, LocalDateTime created,
                          LocalDateTime lastUpdate, long textSize) {
        super(created, lastUpdate);
        this.paragraphs = paragraphs;
        this.metadata = metadata;
        this.textSize = textSize;
    }

    public void toHtml(StringWriter w) {
        for (Paragraph p : this.paragraphs) {
            p.toHtml(w);
        }
    }

    /**
     * Creates preview text of a maximum length without any formatting.
     *
     * @param w         writer into which the text is written
     * @param maxLength maximum number of characters to write
     * @return length of remaining length budget
     * @
     */
    public int toPreview(StringWriter w, int maxLength) {
        for (Paragraph p : this.paragraphs) {
            if (maxLength > 0)
                maxLength = new WebGen().previewText(p, w, maxLength);
        }
        return maxLength;
    }

    /**
     * Returns all paragraphs in this document
     *
     * @return paragraphs
     */
    public List<Paragraph> getParagraphs() {
        return this.paragraphs;
    }


    public String getTitle() {
        //metadata title takes priorty
        if (this.metadata.has("title"))
            return this.metadata.get("title");
        // if there are captions, take the first one
        for (FormattedTextDocument.Paragraph p : this.paragraphs) {
            if (p instanceof Heading h)
                if (h.level() <= 1)
                    return h.text().toPlainText();
        }
        //if the first paragraph is text, let's take the first line
        if (this.paragraphs.size() >= 1) {
            if (this.paragraphs.get(0) instanceof TextParagraph text) {
                String s = text.text().toPlainText();
                if (s.contains("\n")) s = s.substring(0, s.indexOf("\n"));
                if (!"".equals(s.trim()))
                    return s;
            }
        }
        return null;
    }

    public long getTextSize() {
        return this.textSize;
    }

    public interface FormattedTextContent {
        void toHtml(StringWriter w);

    }

    public interface Paragraph extends FormattedTextContent {

    }

    public interface TextFragment extends FormattedTextContent {
        String toPlainText();
    }

    public static class TextFragmentSequence implements TextFragment {

        private final List<TextFragment> fragments;

        private TextFragmentSequence(List<TextFragment> fragments) {
            this.fragments = fragments;
        }

        public static TextFragment create(List<TextFragment> fragments) {
            if (fragments.size() == 1) return fragments.get(0);
            return new TextFragmentSequence(fragments);
        }

        @Override
        public void toHtml(StringWriter w) {
            for (TextFragment t : getFragments()) {
                t.toHtml(w);
            }
        }

        @Override
        public String toPlainText() {
            StringBuilder b = new StringBuilder();
            for (TextFragment t : getFragments()) {
                b.append(t.toPlainText());
            }
            return b.toString();
        }

        public List<TextFragment> getFragments() {
            return this.fragments;
        }
    }

    public static record Heading(TextFragment text, int level) implements Paragraph {

        @Override
        public void toHtml(StringWriter w) {
            int l = this.level + 1;
            w.write("<h" + l + ">");
            this.text.toHtml(w);
            w.write("</h" + l + ">");
        }


    }

    public static record TextParagraph(TextFragment text) implements Paragraph {
        @Override
        public void toHtml(StringWriter w) {
            w.write("<p>");
            this.text.toHtml(w);
            w.write("</p>");
        }
    }

    public static record HorizontalRow() implements Paragraph {
        @Override
        public void toHtml(StringWriter w) {
            w.write("<hr />");
        }

        public int toPreview(StringWriter w, int maxLength) {
            return maxLength;
        }
    }

    public static record BulletList(List<Paragraph> items) implements Paragraph {
        @Override
        public void toHtml(StringWriter w) {
            w.write("<p><ul>");
            for (Paragraph t : this.items) {
                w.write("<li>");
                t.toHtml(w);
                w.write("</li>");
            }
            w.write("</ul></p>");
        }
    }

    public static record BlockQuote(List<Paragraph> paragraphs) implements Paragraph {
        @Override
        public void toHtml(StringWriter w) {
            w.write("<blockquote>");
            for (Paragraph p : this.paragraphs) {
                p.toHtml(w);
            }
            w.write("</blockquote>");
        }
    }

    //    public static record Image(String source, String paragraphs) implements Paragraph {}
    public static record CodeBlock(String source, String language) implements Paragraph {
        @Override
        public void toHtml(StringWriter w) {
            w.write("<pre>");
            w.write(this.source);
            w.write("</pre>");
        }

        public int toPreview(StringWriter w, int maxLength) {
            return maxLength;
        }
    }

    public static record PlainTextFragment(String text) implements TextFragment {
        @Override
        public void toHtml(StringWriter w) {
            w.write(StringEscapeUtils.escapeHtml4(this.text));
        }

        @Override
        public String toPlainText() {
            return this.text;
        }
    }

    public static record InlineImage(String source, TextFragment text) implements TextFragment {
        @Override
        public void toHtml(StringWriter w) {
            w.write("<img src=\"" + this.source + "\" alt=\"");
            this.text.toHtml(w);
            w.write("\" />");
        }

        @Override
        public String toPlainText() {
            return "";
        }
    }


    public static abstract class DecoratedTextFragment implements TextFragment {
        private final TextFragment text;
        private final String htmlOpen;
        private final String htmlClose;

        public DecoratedTextFragment(TextFragment text, String htmlOpen, String htmlClose) {
            this.text = text;
            this.htmlOpen = htmlOpen;
            this.htmlClose = htmlClose;
        }

        @Override
        public void toHtml(StringWriter w) {
            w.write(this.htmlOpen);
            this.text.toHtml(w);
            w.write(this.htmlClose);
        }

        @Override
        public String toPlainText() {
            return this.text.toPlainText();
        }

        public String getHtmlClose() {
            return this.htmlClose;
        }

        public String getHtmlOpen() {
            return this.htmlOpen;
        }

        public TextFragment getText() {
            return this.text;
        }
    }

    public static class EmphasisTextFragment extends DecoratedTextFragment {
        public EmphasisTextFragment(TextFragment text) {
            super(text, "<em>", "</em>");
        }
    }

    public static class StrongEmphasisTextFragment extends DecoratedTextFragment {
        public StrongEmphasisTextFragment(TextFragment text) {
            super(text, "<strong>", "</strong>");
        }
    }

    public static class Link extends DecoratedTextFragment {
        private final String targetURI;

        public Link(String targetURI, TextFragment text) {
            super(text, "<a href=\"" + targetURI + "\">", "</a>");
            this.targetURI = targetURI;
        }
    }
}
