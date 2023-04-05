package edu.cmu.webgen.project;

import edu.cmu.webgen.WebGen;
import edu.cmu.webgen.working.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;

public class Event implements Comparable<Event> {

    protected @NotNull
    final List<AbstractContent> content;
    protected @NotNull
    final String directoryName;
    protected @NotNull
    final Set<Topic> topics = new HashSet<>();
    private final List<Object> innerEvents;
    private final List<SubArticle> innerArticles;
    private final LocalDateTime lastUpdate;
    private final LocalDateTime created;
    protected @Nullable String id = null;
    protected @NotNull Metadata metadata = new Metadata();
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Event(List<AbstractContent> content, List<Object> subEvents, @NotNull List<SubArticle> subArticles,
          @NotNull String directoryName, @NotNull LocalDateTime created, @NotNull LocalDateTime lastUpdate,
          @NotNull LocalDateTime startDate, @NotNull LocalDateTime endDate) {
        this.content = new ArrayList<>(content);
        this.innerEvents = subEvents;
        this.innerArticles = subArticles;

        this.lastUpdate = lastUpdate;
        this.created = created;

        this.directoryName = directoryName;

        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int compareTo(@NotNull Event that) {
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

    public @NotNull List<AbstractContent> getContent() {
        return this.content;
    }

    /**
     * returns the title of this event, either from metadata or from inner content,
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

    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<Object> getInnerEvents() {
        return this.innerEvents;
    }

    public List<SubArticle> getInnerArticles() {
        return this.innerArticles;
    }
}
