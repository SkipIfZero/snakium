package com.skipifzero.snakium.framework.input;

import java.util.ArrayList;
import java.util.List;

/**
 * A class used to pool objects.
 * 
 * Heavily inspired by similar class found in Beginning Android Games by Mario Zechner.
 *  
 * @param <T> the class to pool
 * 
 * @author Peter Hillerström
 * @since 2013-04-21
 * @version 2
 */
public class Pool<T> {

	/**
	 * Interface for a factory to create an object of T type.
	 * @author Peter Hillerström
	 * @version 1
	 * @param <T> the class to create
	 */
	public interface PoolObjectFactory<T> {
		public T createObject();
	}

	private final List<T> freeObjects;
	private final PoolObjectFactory<T> factory;
	private final int maxSize;

	/**
	 * Creates a new pool.
	 * @param factory the factory to creates objects with
	 * @param maxSize the maximum amount of items this pool can hold
	 */
	public Pool(PoolObjectFactory<T> factory, int maxSize) {
		this.factory = factory;
		this.maxSize = maxSize;
		this.freeObjects = new ArrayList<T>(maxSize);
	}

	/**
	 * Returns a recycled object. If there aren't any objects left to reuse it creates a new one.
	 * @return object of T type
	 */
	public T getRecycledObject() {
		//If there aren't any free objects to reuse, make a new one.
		if(freeObjects.size() <= 0){
			return factory.createObject();
		}
		//Return an old object to reuse.
		return freeObjects.remove(freeObjects.size() -1);
	}

	/**
	 * Adds an object to recycle to this pool.
	 * If the pool is full the object will be consumed by the Garbage Collector.
	 * @param object the object to recycle
	 */
	public void recycleObject(T object) {
		//Adds object to list of free objects. If the list is larger than the max size the object will be consumed by the Garbage Collector.
		if (freeObjects.size() < maxSize){
			freeObjects.add(object);
		}
	}
}

