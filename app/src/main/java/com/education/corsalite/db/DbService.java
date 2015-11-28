package com.education.corsalite.db;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Query;
import com.education.corsalite.utils.L;

import java.util.ArrayList;
import java.util.List;

public class DbService {

	private static ObjectContainer objectContainer = null;
	private static EmbeddedConfiguration configuration = null;
	public static final int GLOBAL_QUERY_DEPTH = 1; // cshine: please, don't ever change this; use setActivationDepth()

	/*
	 * deepak, abdul, cshine: static and synchronized to ensure that file locked
	 * exceptions will not occur when multiple threads from the same process
	 * attempt to access this class at near the same time.
	 */
	private static synchronized ObjectContainer GetDb() {
		objectContainer = objectContainer == null ? Db4oEmbedded.openFile(DbService.GetConfig(), DbAdapter.createDbFile()) : objectContainer;
		return objectContainer;
	}

	private static EmbeddedConfiguration GetConfig() {
		configuration = new DbConfigurationBuilder().getConfiguration();
		return configuration;
	}

	/**
	 * This is used by tests only. Do not use this method for any other reason.
	 */
	public void closeDb() {
		objectContainer.close();
		objectContainer = null;
	}

	public void setActivationDepth(int depth) {
		GetDb();
		configuration.common().activationDepth(depth);
	}

	public ObjectContainer ObjectContainer() {
		return GetDb();
	}

	/**
	 * Save an entity to the DB. Entity must be marked with
	 * {@code IDomainEntity}. Remember to set the appropriate rules in the
	 * {@code DbConfigurationBuilder} class so that the data is persisted
	 * properly in child objects.
	 */
	public <T> void Save(final IDomainEntity<T> entity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectContainer db = GetDb();
					db.store(entity);
					db.commit();
					L.info("entity '%s' saved successfully", entity.getClass().getSimpleName());
				}
				catch (Exception e) {
					L.error(e.getMessage(), e);
				}
			}
		}).start();
	}

	/**
	 * Save List of entities of type T to DB. Each entity must be marked with
	 * {@code IDomainEntity}. Remember to set the appropriate rules in the
	 * {@code DbConfigurationBuilder} class so that the data is persisted
	 * properly in child objects.
	 */
	public <T> void Save(final List<T> entity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectContainer db = GetDb();
					for (T iDomainEntity : entity) {
						db.store(iDomainEntity);
					}
					db.commit();
					L.info("List entity '%s' saved successfully", entity.get(0).getClass().getSimpleName());
				} catch (Exception e) {
					L.error(e.getMessage(), e);
				}
			}
		}).start();
	}

	/**
	 * Save List of entities of type T to DB Each entity must be marked with
	 * {@code IDomainEntity}. Remember to set the appropriate rules in the
	 * {@code DbConfigurationBuilder} class so that the data is persisted
	 * properly in child objects.
	 */
	public <T> void Delete(final Class<T> entity) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectContainer db = GetDb();
					List<T> results = db.query(entity);
					for (T iDomainEntity : results) {
						db.delete(iDomainEntity);
					}
					db.commit();
					L.info("entity '%s' deleted successfully", entity.getClass().getSimpleName());
				} catch (Exception e) {
					L.error(e.getMessage(), e);
				}
			}
		}).start();
	}

	public <T> void DeleteById(final Class<T> entity, final String fieldName, final String id) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectContainer db = GetDb();
					Query query = db.query();
					query.constrain(entity);
					query.descend(fieldName).constrain(id);

					ObjectSet<T> result = query.execute();
					for (T item : result) {
						db.delete(item);
					}
					db.commit();
					L.info("entity '%s' deleted successfully", entity.getClass().getSimpleName());
				} catch (Exception e) {
					L.error(e.getMessage(), e);
				}
			}
		}).start();
	}

	public <T> List<T> Get(Class<T> entity) {
		try {
			ObjectContainer db = GetDb();
			return db.query(entity);
		}
		catch (Exception e) {
			L.error(e.getMessage(), e);
			return new ArrayList<T>();
		}
	}

	public <T> T GetById(Class<T> entity, String fieldName, String id) {
		try {
			ObjectContainer db = GetDb();
			Query query = db.query();
			query.constrain(entity);
			query.descend(fieldName).constrain(id);
			ObjectSet<T> result = query.execute();
			if (result == null || result.size() == 0) { return null; }
			return result.get(0);
		}
		catch (Exception e) {
			L.error(e.getMessage(), e);
			return null;
		}
	}

	public <T, U> void DeleteFromList(final IDomainEntity<T> parent, final List<U> childList, final List<U> toDelete, final boolean shouldDeleteChildFromDb) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					childList.removeAll(toDelete);
					ObjectContainer db = GetDb();
					Save(parent);
					if (shouldDeleteChildFromDb) {
						for (U iDomainEntity : toDelete) {
							db.delete(iDomainEntity);
						}
					}
					db.commit();
				} catch (Exception e) {
					L.error(e.getMessage(), e);
				}
			}
		}).start();
	}
}
