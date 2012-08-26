/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.form.builder.services.impl.db;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.jbpm.form.builder.ng.model.client.Settings;
import org.jbpm.form.builder.services.annotations.FormPersistence;
import org.jbpm.form.builder.services.api.SettingsService;

/**
 *
 */
public class DBSettingsService implements SettingsService {

    @Inject @FormPersistence
    private EntityManager em;

    public DBSettingsService() {
    }

    public Settings getSettingsByUserId(String userName) {
        Query query = em.createNamedQuery("GetSettingsByUser");

        query.setParameter("userId", userName);
        Settings settings = null;

        try {
            settings = (Settings) query.getSingleResult();
        } catch (NoResultException ex) {
            try {

                settings = new Settings(userName);
                em.persist(settings);

            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
        }

        return settings;
    }

    public void applySettings(Settings settings, String userName) {

        try {
            em.merge(settings);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
