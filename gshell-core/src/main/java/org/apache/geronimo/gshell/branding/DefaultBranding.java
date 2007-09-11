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

package org.apache.geronimo.gshell.branding;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.geronimo.gshell.ansi.Buffer;
import org.apache.geronimo.gshell.ansi.Code;
import org.apache.geronimo.gshell.ansi.RenderWriter;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * Provides the default branding for GShell.
 *
 * @version $Rev$ $Date$
 */
@Component(role=Branding.class)
public class DefaultBranding
    implements Branding
{
    @Requirement
    private VersionLoader versionLoader;

    public String getName() {
        return "gshell";
    }

    public String getDisplayName() {
        return "GShell";
    }

    public File getUserDirectory() {
        File userHome = new File(System.getProperty("user.home"));

        File dir = new File(userHome, "." + getName());

        return dir.getAbsoluteFile();
    }

    public File getSharedDirectory() {
        //
        // FIXME: This is not very portable :-(
        //
        
        File dir = new File("/etc", getName());

        return dir.getAbsoluteFile();
    }

    public String getAbout() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);

        out.println("For information about Apache Geronimo, visit:");
        out.println("    http://geronimo.apache.org ");
        out.flush();

        return writer.toString();
    }

    public String getVersion() {
        assert versionLoader != null;

        return versionLoader.getVersion();
    }

    public String getWelcomeBanner() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new RenderWriter(writer);
        Buffer buff = new Buffer();

        /*
        String[] banner = {
            "   ____ ____  _          _ _ ",
            "  / ___/ ___|| |__   ___| | |",
            " | |  _\\___ \\| '_ \\ / _ \\ | |",
            " | |_| |___) | | | |  __/ | |",
            "  \\____|____/|_| |_|\\___|_|_|",
        };
        */

        String[] banner = {
            "                          ,,                 ,,    ,,",
            "   .g8\"\"\"bgd   .M\"\"\"bgd `7MM               `7MM  `7MM",
            " .dP'     `M  ,MI    \"Y   MM                 MM    MM",
            " dM'       `  `MMb.       MMpMMMb.  .gP\"Ya   MM    MM",
            " MM             `YMMNq.   MM    MM ,M'   Yb  MM    MM",
            " MM.    `7MMF'.     `MM   MM    MM 8M\"\"\"\"\"\"  MM    MM",
            " `Mb.     MM  Mb     dM   MM    MM YM.    ,  MM    MM",
            "   `\"bmmmdPY  P\"Ybmmd\"  .JMML  JMML.`Mbmmd'.JMML..JMML."
        };

        for (String line : banner) {
            buff.attrib(line, Code.CYAN);
            out.println(buff);
        }
        
        out.println();
        out.println(" @|bold GShell| (" + getVersion() + ")");
        out.println();
        out.println("Type '@|bold help|' for more information.");
        out.flush();

        return writer.toString();
    }

    public String getProfileScriptName() {
        return getName() + ".profile";
    }

    public String getInteractiveScriptName() {
        return getName() + ".rc";
    }

    public String getHistoryFileName() {
        return getName() + ".history";
    }

    public String getPropertyName(final String name) {
        assert name != null;
        
        return getName() + "." + name;
    }

    public String getProperty(final String name) {
        return System.getProperty(getPropertyName(name));
    }

    public String getProperty(final String name, final String defaultValue) {
        String value = getProperty(name);
        
        if (value == null) {
            return defaultValue;
        }

        return value;
    }
}