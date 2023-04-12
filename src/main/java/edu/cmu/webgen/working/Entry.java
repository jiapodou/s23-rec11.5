package edu.cmu.webgen.working;

import edu.cmu.webgen.project.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * An entry represents a directory with content that can be rendered as a page.
 * <p>
 * Right now, two forms of entries are supported: Articles and Events. Events are recognized by metadata that indicates `eventBegin` or `eventEnd`
 * <p>
 * Content inside an article may include other articles or events.
 */
public abstract class Entry extends Node implements Comparable<Entry> {
    private static final Map<String, Integer> idCounter = new HashMap<>();
    protected @NotNull
    final List<Node> inner;
    protected @NotNull
    final String directoryName;
    protected @NotNull
    final Set<Topic> topics = new HashSet<>();
    protected @Nullable String id = null;
    protected @NotNull Metadata metadata = new Metadata();


    Entry(@NotNull List<Node> inner, @NotNull String directoryName, @NotNull LocalDateTime created, @NotNull LocalDateTime lastUpdate) {
        super(created, lastUpdate);
        this.inner = new ArrayList<>(inner);

        this.directoryName = directoryName;
    }

    private static String genId(String title) {
        String id = title.toLowerCase().replaceAll("[^a-z0-9]", "_");
        if (idCounter.containsKey(id)) {
            int idIdx = idCounter.get(id) + 1;
            idCounter.put(id, idIdx);
            id = id + idIdx;
        } else
            idCounter.put(id, 1);
        return id;
    }

    public int compareTo(@NotNull Entry that) {
        return this.getTitle().compareTo(that.getTitle());
    }

    /**
     * return an unique ID of letters, digits and underscores only, based on the title
     *
     * @return the id
     */
    public String getId() {
        if (id == null)
            id = genId(getTitle());
        return id;
    }

    /**
     * get the most recent update of this folder or any content inside
     *
     * @return timestamp of last update
     */
    public @NotNull LocalDateTime getLastUpdate() {
        Optional<LocalDateTime> innerLastUpdate = inner.stream().map(Node::getLastUpdate).max(LocalDateTime::compareTo);
        if (innerLastUpdate.isPresent() && innerLastUpdate.get().compareTo(super.getLastUpdate()) > 0)
            return innerLastUpdate.get();
        else return super.getLastUpdate();
    }

    /**
     * get the oldest creation date of this folder or any content inside
     *
     * @return timestamp of last creation date
     */
    public @NotNull LocalDateTime getCreated() {
        Optional<LocalDateTime> innerCreated = inner.stream().map(Node::getCreated).min(LocalDateTime::compareTo);
        if (innerCreated.isPresent() && innerCreated.get().compareTo(super.getCreated()) < 0)
            return innerCreated.get();
        else return super.getCreated();
    }

    public @NotNull List<Node> getInner() {
        return inner;
    }

    @Override
    public boolean hasTitle() {
        return true;
    }

    /**
     * returns the title of this entry, either from metadata or from inner content,
     * or if those don't exist the directory name
     *
     * @return the title
     */
    public @NotNull String getTitle() {
        if (metadata.has("title"))
            return metadata.get("title");
        for (Node n : inner)
            if (n.isContent() && n.hasTitle())
                return Objects.requireNonNull(n.getTitle());
        return directoryName;
    }

    public Set<Topic> getTopics() {
        Set<Topic> result = new HashSet<>();
        result.addAll(this.topics);
        for (Node i : inner) {
            result.addAll(i.getTopics());
        }
        return result;
    }

    @Override
    public boolean isContent() {
        return false;
    }

    public long getSize() {
        int size = 0;
        for (Node s : inner)
            size += s.getSize();
        return size;
    }

    public void addMetadata(Metadata m) {
        this.metadata = this.metadata.concat(m);
        this.topics.addAll(Topic.from(m));
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void addChild(Node content) {
        inner.add(content);
    }

    public abstract Stream<? extends Article> getAllArticles();

}