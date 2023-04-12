package edu.cmu.webgen.project;

import edu.cmu.webgen.WebGen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * An article is a key element of a web page. May contain inner content, such as text, inner articles, and events.
 */
public class Article implements Comparable<Article> {
    protected @NotNull
    final List<SubArticle> innerArticles;
    protected @NotNull
    final String directoryName;
    protected @NotNull
    final Set<Topic> topics = new HashSet<>();
    //    final List<Event> innerEvents;
    final List<AbstractContent> content;
    private final LocalDateTime lastUpdate;
    private final LocalDateTime created;
    protected @Nullable String id = null;
    protected @NotNull Metadata metadata = new Metadata();

    public Article(List<AbstractContent> content, @NotNull List<SubArticle> subArticles, @NotNull String directoryName,
            @NotNull LocalDateTime created, @NotNull LocalDateTime lastUpdate) {
        this.content = content;
        this.lastUpdate = lastUpdate;
        this.created = created;
        this.innerArticles = new ArrayList<>(subArticles);
        this.directoryName = directoryName;
    }

    public int compareTo(@NotNull Article that) {
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
        Optional<LocalDateTime> innerLastUpdateArticle = this.innerArticles
                .stream().map(SubArticle::getLastUpdate).max(LocalDateTime::compareTo);
        LocalDateTime last = this.lastUpdate;
        if (innerLastUpdateArticle.isPresent() && innerLastUpdateArticle.get().compareTo(last) > 0)
            last = innerLastUpdateArticle.get();
        return last;
    }

    /**
     * get the oldest creation date of this folder or any content inside
     *
     * @return timestamp of last creation date
     */
    public @NotNull LocalDateTime getCreated() {
        Optional<LocalDateTime> innerCreatedArticle = this.innerArticles
                .stream().map(SubArticle::getCreated).max(LocalDateTime::compareTo);
//        Optional<LocalDateTime> innerCreatedEvent = innerEvents.stream().map(Event::getCreated).max(LocalDateTime::compareTo);
        LocalDateTime last = this.created;
        if (innerCreatedArticle.isPresent() && innerCreatedArticle.get().compareTo(last) > 0)
            last = innerCreatedArticle.get();
//        if (innerCreatedEvent.isPresent() && innerCreatedEvent.get().compareTo(last) > 0)
//            last = innerCreatedEvent.get();
        return last;
    }

    public @NotNull List<SubArticle> getInnerArticles() {
        return this.innerArticles;
    }


    /**
     * returns the title of this article, either from metadata or from inner content,
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
        this.topics.addAll(Topic.from(m));
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

    public boolean isArticlePinned() {
        return getMetadata().has("pinned") && !getMetadata().get("pinned").equals("false");
    }
}
