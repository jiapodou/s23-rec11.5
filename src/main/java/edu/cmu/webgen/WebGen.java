package edu.cmu.webgen;

import com.github.jknack.handlebars.internal.text.StringEscapeUtils;

import edu.cmu.webgen.parser.ProjectParser;
import edu.cmu.webgen.project.*;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class WebGen {

    private static final Map<String, Integer> idCounter = new HashMap<>();
    public static void main(String[] args) {
        try {
            WebGenArgs options = new WebGenArgs(args);
            if (!options.projectSourceDirectoryExists() || options.isHelp()) {
                options.printHelp();
                return;
            }
            Project project = new ProjectParser().loadProject(options.getProjectSourceDirectory());
            new CLI(project).run(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String genId(String title) {
        String id = title.toLowerCase().replaceAll("[^a-z0-9]", "_");
        if (idCounter.containsKey(id)) {
            int idIdx = idCounter.get(id) + 1;
            idCounter.put(id, idIdx);
            id = id + idIdx;
        } else
            idCounter.put(id, 1);
        return id;
    }

    /**
     * helper function to paginate content
     *
     * @param content  iterator of content
     * @param pageSize number of elements per page
     * @param <R>      type of the content
     * @return list of lists of content, where each inner list has pageSize entries (possibly except the last)
     */
    public static <R> List<List<R>> paginateContent(Iterator<R> content, int pageSize) {
        List<List<R>> result = new ArrayList<>();
        List<R> pageContent = new ArrayList<>(pageSize);
        while (content.hasNext()) {
            if (pageContent.size() >= pageSize) {
                result.add(pageContent);
                pageContent = new ArrayList<>(pageSize);
            }
            pageContent.add(content.next());
        }
        result.add(pageContent);
        return result;
    }

    static final DateTimeFormatter formatter =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);

    public int previewText(FormattedTextDocument.FormattedTextContent content, StringWriter w, int maxLength) {
        if (content instanceof FormattedTextDocument.TextFragmentSequence node) {
            for (FormattedTextDocument.TextFragment t : node.getFragments()) {
                if (maxLength > 0) {
                    maxLength = previewText(t, w, maxLength);
                }
            }
            return maxLength;
        }
        if (content instanceof FormattedTextDocument.Heading node) {
            int l = node.level() + 1;
            w.write("<p><strong class=\"previewh" + l + "\">");
            maxLength = previewText(node.text(), w, maxLength);
            w.write("</strong></p>");
            return maxLength;
        }
        if (content instanceof FormattedTextDocument.TextParagraph node) {
            w.write("<p>");
            maxLength = previewText(node.text(), w, maxLength);
            w.write("</p>");
            return maxLength;
        }
        if (content instanceof FormattedTextDocument.BulletList node) {
            w.write("<p><ul>");
            for (FormattedTextDocument.Paragraph t : node.items()) {
                if (maxLength > 0) {
                    w.write("<li>");
                    maxLength = previewText(t, w, maxLength);
                    w.write("</li>");
                }
            }
            w.write("</ul></p>");
            return maxLength;
        }
        if (content instanceof FormattedTextDocument.BlockQuote node) {
            w.write("<blockquote>");
            for (FormattedTextDocument.Paragraph p : node.paragraphs()) {
                maxLength = previewText(p, w, maxLength);
            }
            w.write("</blockquote>");
            return maxLength;
        }
        if (content instanceof FormattedTextDocument.PlainTextFragment node) {
            if (node.text().length() > maxLength) {
                w.write(StringEscapeUtils.escapeHtml4(node.text().substring(0, maxLength)));
                w.write("...");
                return 0;
            }
            w.write(StringEscapeUtils.escapeHtml4(node.text()));
            return maxLength - node.text().length();
        }
        if (content instanceof FormattedTextDocument.InlineImage node) {
            return maxLength;
        }
        if (content instanceof FormattedTextDocument.DecoratedTextFragment node) {
            w.write(node.getHtmlOpen());
            maxLength = previewText(node.getText(), w, maxLength);
            w.write(node.getHtmlClose());
            return maxLength;
        }

        throw new UnsupportedOperationException("Preview for content %s of type %s not supported".formatted(
                content, content.getClass().getSimpleName()));
    }

    public List<Object> findArticlesByTopic(Project project, Topic topic) {
        List<Object> result = new ArrayList<>();
        for (Article a : project.getArticles()) {
            if (project.getTopics(a).contains(topic))
                result.add(a);
            for (SubArticle sa : a.getInnerArticles()) {
                if (project.getTopics(sa).contains(topic))
                    result.add(sa);
                for (SubSubArticle ssa : sa.getInnerArticles()) {
                    if (project.getTopics(ssa).contains(topic))
                        result.add(ssa);
                }
            }
        }
        return result;
    }

}
