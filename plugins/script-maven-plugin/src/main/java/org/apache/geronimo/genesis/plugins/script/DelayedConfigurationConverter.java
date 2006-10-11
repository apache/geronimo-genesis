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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.component.configurator.ComponentConfigurationException;
import org.codehaus.plexus.component.configurator.ConfigurationListener;
import org.codehaus.plexus.component.configurator.converters.AbstractConfigurationConverter;
import org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.codehaus.plexus.configuration.PlexusConfiguration;

/**
 * Plexus converter for {@link DelayedConfiguration} objects.
 *
 * @version $Rev$ $Date$
 */
public class DelayedConfigurationConverter
    extends AbstractConfigurationConverter
{
    private static final Log log = LogFactory.getLog(DelayedConfigurationConverter.class);

    public boolean canConvert(final Class type) {
        return DelayedConfiguration.class.isAssignableFrom(type);
    }

    public Object fromConfiguration(final ConverterLookup converterLookup,
                                    final PlexusConfiguration configuration,
                                    final Class type,
                                    final Class baseType,
                                    final ClassLoader classLoader,
                                    final ExpressionEvaluator expressionEvaluator,
                                    final ConfigurationListener listener)
            throws ComponentConfigurationException
    {
        if (log.isDebugEnabled()) {
            log.debug("Capturing delayed config: " + configuration);
        }

        return new DelayedConfiguration(converterLookup,
                                        configuration,
                                        type,
                                        baseType,
                                        classLoader,
                                        expressionEvaluator,
                                        listener);
    }
}