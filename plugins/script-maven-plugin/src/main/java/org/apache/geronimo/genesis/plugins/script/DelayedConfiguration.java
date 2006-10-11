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

package org.apache.geronimo.genesis.plugins.script;

import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Container that captures a custom Plexus configuration for delayed processing.
 *
 * @version $Rev$ $Date$
 */
public class DelayedConfiguration
{
    private static final Log log = LogFactory.getLog(DelayedConfiguration.class);

    private ConverterLookup converterLookup;

    private PlexusConfiguration configuration;

    private Class type;

    private Class baseType;

    private ClassLoader classLoader;

    private ExpressionEvaluator expressionEvaluator;

    private ConfigurationListener listener;

    public DelayedConfiguration(final ConverterLookup converterLookup,
                                final PlexusConfiguration configuration,
                                final Class type,
                                final Class baseType,
                                final ClassLoader classLoader,
                                final ExpressionEvaluator expressionEvaluator,
                                final ConfigurationListener listener)
    {
        this.converterLookup = converterLookup;
        this.configuration = configuration;
        this.type = type;
        this.baseType = baseType;
        this.classLoader = classLoader;
        this.expressionEvaluator = expressionEvaluator;
        this.listener = listener;
    }

    public String toString() {
        return String.valueOf(getConfiguration());
    }

    public ConverterLookup getConverterLookup() {
        return converterLookup;
    }

    public PlexusConfiguration getConfiguration() {
        return configuration;
    }

    public Class getType() {
        return type;
    }

    public Class getBaseType() {
        return baseType;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public ExpressionEvaluator getExpressionEvaluator() {
        return expressionEvaluator;
    }

    public ConfigurationListener getListener() {
        return listener;
    }
}
