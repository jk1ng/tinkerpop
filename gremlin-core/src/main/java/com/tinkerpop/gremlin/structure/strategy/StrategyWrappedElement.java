package com.tinkerpop.gremlin.structure.strategy;

import com.tinkerpop.gremlin.process.graph.GraphTraversal;
import com.tinkerpop.gremlin.structure.Element;
import com.tinkerpop.gremlin.structure.Property;
import com.tinkerpop.gremlin.structure.util.ElementHelper;
import com.tinkerpop.gremlin.util.StreamFactory;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
public abstract class StrategyWrappedElement implements Element, StrategyWrapped {
    protected final StrategyWrappedGraph strategyWrappedGraph;
    private final Element baseElement;
    private final Strategy.Context<StrategyWrappedElement> elementStrategyContext;

    protected StrategyWrappedElement(final Element baseElement, final StrategyWrappedGraph strategyWrappedGraph) {
        if (baseElement instanceof StrategyWrapped) throw new IllegalArgumentException(
                String.format("The element %s is already StrategyWrapped and must be a base Element", baseElement));
        this.strategyWrappedGraph = strategyWrappedGraph;
        this.baseElement = baseElement;
        this.elementStrategyContext = new Strategy.Context<>(strategyWrappedGraph.getBaseGraph(), this);
    }

    public Element getBaseElement() {
        return this.baseElement;
    }

    @Override
    public <V> V value(final String key) throws NoSuchElementException {
        return this.strategyWrappedGraph.strategy().compose(
                s -> s.<V>getElementValue(elementStrategyContext),
                this.baseElement::value).apply(key);
    }

    @Override
    public <V> Property<V> property(final String key) {
        return new StrategyWrappedProperty<>(this.strategyWrappedGraph.strategy().compose(
                s -> s.<V>getElementGetProperty(elementStrategyContext),
                this.baseElement::property).apply(key), this.strategyWrappedGraph);
    }

    @Override
    public <V> Iterator<? extends Property<V>> properties(final String... propertyKeys) {
        return StreamFactory.stream(this.strategyWrappedGraph.strategy().compose(
                s -> s.getElementPropertiesGetter(elementStrategyContext),
                () -> this.baseElement.iterators().properties(propertyKeys)).get())
                .map(property -> new StrategyWrappedProperty<>((Property<V>) property, strategyWrappedGraph)).iterator();
    }

    @Override
    public <V> Iterator<? extends Property<V>> hiddens(final String... propertyKeys) {
        return StreamFactory.stream(this.strategyWrappedGraph.strategy().compose(
                s -> s.getElementHiddens(elementStrategyContext),
                () -> this.baseElement.iterators().hiddens(propertyKeys)).get())
                .map(property -> new StrategyWrappedProperty<>((Property<V>) property, strategyWrappedGraph)).iterator();
    }

    @Override
    public Set<String> keys() {
        return this.strategyWrappedGraph.strategy().compose(
                s -> s.getElementKeys(elementStrategyContext),
                this.baseElement::keys).get();
    }

    @Override
    public Set<String> hiddenKeys() {
        return this.strategyWrappedGraph.strategy().compose(
                s -> s.getElementHiddenKeys(elementStrategyContext),
                this.baseElement::hiddenKeys).get();
    }

    @Override
    public <V> Iterator<V> values(final String... propertyKeys) {
        return this.strategyWrappedGraph.strategy().compose(
                s -> s.getElementValues(elementStrategyContext),
                this.baseElement.values(propertyKeys)).get();
    }

    @Override
    public <V> Iterator<V> hiddenValues(final String... propertyKeys) {
        return this.strategyWrappedGraph.strategy().compose(
                s -> s.getElementHiddenValues(elementStrategyContext),
                this.baseElement.hiddenValues(propertyKeys)).get();
    }

    @Override
    public String label() {
        return this.strategyWrappedGraph.strategy().compose(
                s -> s.getElementLabel(elementStrategyContext),
                this.baseElement::label).get();
    }

    @Override
    public Object id() {
        return this.strategyWrappedGraph.strategy().compose(
                s -> s.getElementId(elementStrategyContext),
                this.baseElement::id).get();
    }

    @Override
    public <V> Property<V> property(final String key, final V value) {
        return this.strategyWrappedGraph.strategy().compose(
                s -> s.<V>getElementProperty(elementStrategyContext),
                this.baseElement::property).apply(key, value);
    }

    @Override
    public void remove() {
        this.strategyWrappedGraph.strategy().compose(
                s -> s.getRemoveElementStrategy(elementStrategyContext),
                () -> {
                    this.baseElement.remove();
                    return null;
                }).get();
    }

    @Override
    public String toString() {
        return baseElement.toString();
    }

    @Override
    public int hashCode() {
        return this.id().hashCode();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(final Object object) {
        return ElementHelper.areEqual(this, object);
    }

    protected <S, E> GraphTraversal<S, E> applyStrategy(final GraphTraversal<S, E> traversal) {
        traversal.strategies().register(new StrategyWrappedTraversalStrategy(this.strategyWrappedGraph));
        this.strategyWrappedGraph.strategy().getGraphStrategy().ifPresent(s -> s.applyStrategyToTraversal(traversal));
        return traversal;
    }
}
