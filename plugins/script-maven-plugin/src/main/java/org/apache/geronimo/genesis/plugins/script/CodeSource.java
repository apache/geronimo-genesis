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

import java.net.URL;
import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * COnfiguration for a scripts code source.
 *
 * @version $Rev$ $Date$
 */
public class CodeSource
{
    private URL url = null;

    private File file = null;

    private String body = null;

    public String toString() {
        return "{ url: " + url +
               ", file: " + file +
               ", body: " + body +
               " }";
    }

    public URL getUrl() {
        return url;
    }

    public File getFile() {
        return file;
    }

    public String getBody() {
        return body;
    }

    public void validate() throws MojoExecutionException {
        if (url == null && file == null && (body == null || body.trim().length() == 0)) {
            throw new MojoExecutionException("Must specify one of: file, url or body");
        }

        int count = 0;
        if (url != null) {
            count++;
        }
        if (file != null) {
            count++;
        }
        if (body != null) {
            count++;
        }

        if (count != 1) {
            throw new MojoExecutionException("Can only specify one of: file, url or body");
        }
    }
}
