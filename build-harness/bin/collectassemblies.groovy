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

//
// $Rev$ $Date$
//

class AssemblyCollector
    extends CliSupport
{
    def assembliesdir = new File(basedir, "assemblies")
    
    def main(args) {
        def iter = args.toList().iterator()
        def dir
        
        while (iter.hasNext()) {
            def arg = iter.next()
            
            switch (arg) {
                // HACK: Groovy's use of commons-cli eats up an '--' so need to use '---' to skip
                case '---':
                    while (iter.hasNext()) {
                        args.add(iter.next())
                    }
                    break
                
                default:
                    if (dir != null) {
                        throw new Exception("Unexpected argument: ${arg}")
                    }
                    if (arg == ".") {
                        dir = basedir
                    }
                    else {
                        dir = new File(arg)
                        if (!dir.isAbsolute()) {
                            dir = new File(basedir, arg)
                        }
                    }
                    break
            }
        }
        
        assert dir != null
        
        ant.mkdir(dir: assembliesdir)
        
        ant.copy(todir: assembliesdir, flatten: true) {
            fileset(dir: dir) {
                include(name: "**/target/*.zip")
                include(name: "**/target/*.tar.gz")
            }
        }
    }
}

new AssemblyCollector().main(args)
