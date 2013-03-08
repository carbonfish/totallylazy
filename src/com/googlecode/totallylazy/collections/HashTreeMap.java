package com.googlecode.totallylazy.collections;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Callable2;
import com.googlecode.totallylazy.Callables;
import com.googlecode.totallylazy.Functions;
import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Predicates;
import com.googlecode.totallylazy.Segment;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.annotations.multimethod;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.googlecode.totallylazy.Sequences.sequence;

public class HashTreeMap<K, V> extends AbstractMap<K, V> {
    private final PersistentSortedMap<Integer, PersistentMap<K, V>> hash;

    private HashTreeMap(PersistentSortedMap<Integer, PersistentMap<K, V>> hash) {
        this.hash = hash;
    }

    public static <K extends Comparable<K>, V> HashTreeMapFactory<K, V> factory() {
        return new HashTreeMapFactory<K, V>();
    }

    public static <K, V> HashTreeMap<K, V> hashTreeMap(PersistentSortedMap<Integer, PersistentMap<K, V>> map) {
        return new HashTreeMap<K, V>(map);
    }

    public static <K, V> HashTreeMap<K, V> hashTreeMap() {
        return new HashTreeMap<K, V>(PersistentSortedMap.constructors.<Integer, PersistentMap<K, V>>sortedMap());
    }

    public static <K, V> HashTreeMap<K, V> hashTreeMap(Iterable<? extends Pair<K, V>> values) {
        return sequence(values).fold(HashTreeMap.<K,V>hashTreeMap(), Segment.functions.<Pair<K,V>, HashTreeMap<K,V>>cons());
    }

    @Override
    public PersistentMap<K, V> empty() {
        return hashTreeMap();
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Pair<K, V> head() throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Option<Pair<K, V>> headOption() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PersistentMap<K, V> cons(Pair<K, V> head) {
        return put(head.first(), head.second());
    }

    @Override
    public PersistentMap<K, V> tail() throws NoSuchElementException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Option<V> get(final K key) {
        return hash.get(key.hashCode()).flatMap(PersistentMap.functions.<K, V>get(key));
    }

    @Override
    public PersistentMap<K, V> put(K key, V value) {
        int hashCode = key.hashCode();
        return hashTreeMap(hash.put(hashCode, hash.get(hashCode).getOrElse(ListMap.<K, V>emptyListMap()).put(key, value)));
    }

    @Override
    public PersistentMap<K, V> remove(K key) {
        int hashCode = key.hashCode();
        return hashTreeMap(hash.put(hashCode, hash.get(hashCode).getOrElse(ListMap.<K, V>emptyListMap()).remove(key)));
    }

    @Override
    public PersistentMap<K, V> filterKeys(Predicate<? super K> predicate) {
        return hashTreeMap(toSequence().filter(Predicates.<K>first(predicate)));
    }

    @Override
    public PersistentMap<K, V> filterValues(Predicate<? super V> predicate) {
        return hashTreeMap(toSequence().filter(Predicates.<V>second(predicate)));
    }

    @Override
    public <NewV> PersistentMap<K, NewV> map(Callable1<? super V, ? extends NewV> transformer) {
        return hashTreeMap(toSequence().map(Callables.<K,V, NewV>second(transformer)));
    }

    @Override
    public <S> S fold(S seed, Callable2<? super S, ? super Pair<K, V>, ? extends S> callable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Pair<K, V>> iterator() {
        return hash.values().flatMap(Sequences.<Pair<K, V>>identity()).iterator();
    }

    @Override
    public boolean contains(final K other) {
        return hash.get(other.hashCode()).map(PersistentMap.functions.<K,V>contains(other)).getOrElse(false);
    }

    @Override
    public boolean exists(Predicate<? super K> predicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        return toSequence().hashCode();
    }

    @multimethod
    public boolean equals(HashTreeMap<K,V> obj) {
        return toSequence().equals(obj.toSequence());
    }

    @Override
    public String toString() {
        return toSequence().toString("");
    }
}