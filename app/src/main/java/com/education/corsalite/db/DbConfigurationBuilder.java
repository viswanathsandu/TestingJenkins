package com.education.corsalite.db;

import com.db4o.Db4oEmbedded;
import com.db4o.config.EmbeddedConfiguration;
import com.education.corsalite.models.responsemodels.BaseModel;

public class DbConfigurationBuilder {
	/**
	 * Used to perform meta operations for our DB such as creating indexes and
	 * applying cascade rules. Examples are listed inside the method.
	 */
	public EmbeddedConfiguration getConfiguration() {

		EmbeddedConfiguration newConfiguration = Db4oEmbedded.newConfiguration();
		newConfiguration.common().objectClass(BaseModel.class).updateDepth(2);
		newConfiguration.common().activationDepth(DbService.GLOBAL_QUERY_DEPTH);

		return newConfiguration;
	}
}
