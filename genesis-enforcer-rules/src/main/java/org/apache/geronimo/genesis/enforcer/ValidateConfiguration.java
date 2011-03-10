/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.apache.geronimo.genesis.enforcer;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

/**
 * @version $Rev$ $Date$
 */
public class ValidateConfiguration implements EnforcerRule {


    public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {

        try {
            String groupId = (String) helper.evaluate( "${project.groupId}" );
            //do not apply validation to genesis projects as i
            if (groupId.startsWith("org.apache.geronimo.genesis")) {
                return;
            }
            check(helper, "project.description", "project/description", "Genesis provides");
            check(helper, "project.scm.connection", "project/scm/connection", "scm:svn:http://svn.apache.org/repos/asf/geronimo/genesis");
            check(helper, "project.scm.developerConnection", "project/scm/developerConnection", "scm:svn:https://svn.apache.org/repos/asf/geronimo/genesis");
            check(helper, "project.scm.url", "project/scm/url", "http://svn.apache.org/viewvc/geronimo/geronimo/genesis");
        } catch (ExpressionEvaluationException e) {

        }

    }

    private void check(EnforcerRuleHelper helper, String path, String xmlPath, String prefix) throws ExpressionEvaluationException, EnforcerRuleException {
        String actual = (String) helper.evaluate( "${" + path + "}" );
        if (actual == null || actual.startsWith(prefix)) {
            throw new EnforcerRuleException(xmlPath + " must be supplied");
        }
    }

    public boolean isCacheable() {
        return true;
    }

    public boolean isResultValid(EnforcerRule enforcerRule) {
        return true;
    }

    public String getCacheId() {
        return "";
    }
}
