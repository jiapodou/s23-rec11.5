package edu.cmu.webgen.project;

import edu.cmu.webgen.WebGen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * A SubSubArticle is a sub-article of a sub-article. While it would be nice if it could have further articles
 * inside it, it seems silly to create more classes for SubSubSubArticles and so forth, hence stopping here for
 * now.
 */
public class SubSubArticle implements Comparable<SubSubArticle> {

    protected @NotNull
    final String directoryName;
    protected @NotNull
//    final List<Event> innerEvents;
    final List<AbstractContent> content;
    private final LocalDateTime lastUpdate;
    private final LocalDateTime created;
    protected @Nullable String id = null;
    protected @NotNull Metadata metadata = new Metadata();
    private SubArticle parent = null;

    public SubSubArticle(List<AbstractContent> content, @NotNull String directoryName, @NotNull LocalDateTime created,
                  @NotNull LocalDateTime lastUpdate) {
        this.content = content;
        this.lastUpdate = lastUpdate;
        this.created = created;
        this.directoryName = directoryName;
    }

    public int compareTo(@NotNull SubSubArticle that) {
        return this.getTitle().compareTo(that.getTitle());
    }

    /**
     * return an unique ID of letters, digits and underscores only, based on the title
     *
     * @return the id
     */
    public String getId() {
        if (this.id == null)
            this.id = WebGen.genId(getTitle());
        return this.id;
    }

    /**
     * get the most recent update of this folder or any content inside
     *
     * @return timestamp of last update
     */
    public @NotNull LocalDateTime getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * get the oldest creation date of this folder or any content inside
     *
     * @return timestamp of last creation date
     */
    public @NotNull LocalDateTime getCreated() {
        return this.created;
    }

    /**
     * returns the title of this subsubarticle, either from metadata or from inner content,
     * or if those don't exist the directory name
     *
     * @return the title
     */
    public @NotNull String getTitle() {
        if (this.metadata.has("title"))
            return this.metadata.get("title");
        for (AbstractContent n : this.content)
            if (n.hasTitle())
                return Objects.requireNonNull(n.getTitle());
        return this.directoryName;
    }

    public void addMetadata(Metadata m) {
        this.metadata = this.metadata.concat(m);
    }

    public Metadata getMetadata() {
        return this.metadata;
    }

    public void addContent(AbstractContent newcontent) {
        this.content.add(newcontent);
    }


    public LocalDateTime getPublishedDate() {
        if (this.metadata.has("date")) {
            try {
                return WebGen.parseDate(this.metadata.get("date"));
            } catch (ParseException e) {
                System.err.println(e.getMessage());
            }
        }
        return getLastUpdate();
    }

    public List<AbstractContent> getContent() {
        return this.content;
    }

    /**
     * parent of this SubSubArticle.
     * <p>
     * Should not be null if initialized correctly by the {@link ProjectBuilder}.
     */
    public SubArticle getParent() {
        return this.parent;
    }

    public void setParent(SubArticle parent) {
        this.parent = parent;
    }

}
