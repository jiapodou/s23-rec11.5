package edu.cmu.webgen.working;

import edu.cmu.webgen.project.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Represents some form of content in this project
 */
public abstract class Node {

    private final LocalDateTime lastUpdate;
    private final LocalDateTime created;
    protected @NotNull Optional<Entry> parent = Optional.empty();

    protected Node(LocalDateTime created, LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
        this.created = created;
    }

    /**
     * size of the content this node represents in bytes
     *
     * @return the size
     */
    abstract public long getSize();

    /**
     * timestamp of the last update of the content this node represents
     *
     * @return the timestamp
     */
    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    /**
     * timestamp of the content this node represents was created
     *
     * @return the timestamp
     */
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * topics this content is associated with
     *
     * @return the topics
     */
    protected abstract Set<Topic> getTopics();

    /**
     * returns whether this node represents content within
     * an entry
     *
     * @return true if it represents content
     */
    public abstract boolean isContent();

    public boolean hasTitle() {
        return getTitle() != null;
    }

    /**
     * title of this content, if any
     *
     * @return title or null if this content has no title
     */
    public @Nullable String getTitle() {
        return null;
    }

    /**
     * parent of this content
     *
     * @return parent of this content, Empty if none
     */
    public @NotNull Optional<Entry> getParent() {
        return parent;
    }

    /**
     * set the parent for this content
     *
     * @param parent new parent
     */
    public void setParent(Entry parent) {
        this.parent = Optional.of(parent);
    }

    /**
     * collects all articles within this node (including this one if it is an article),
     * if any
     *
     * @return stream of all articles
     */
    public abstract Stream<? extends Article> getAllArticles();

    public List<Entry> getParentsList() {
        List<Entry> result = new ArrayList<>();
        @NotNull Optional<Entry> p = parent;
        while (p.isPresent()) {
            result.add(p.get());
            p = p.get().getParent();
        }
        return result;
    }

}