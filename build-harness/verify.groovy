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

class VerifyOutputs
{
    def basedir = new File(".").getCanonicalFile()
    
    def outdir = new File(basedir, "output")
    
    def ant = new AntBuilder()
    
    def verifyOutput(dir) {
        println "Verifying output from: ${dir}"
        
        def scanner = ant.fileScanner {
            fileset(dir: dir) {
                include(name: "**")
            }
        }
        
        def l = dir.path.length() + 1
        scanner.each { file ->
            def path = file.getPath()[l .. -1]
            println "   ${path}"
        }
    }
    
    def main(args) {
        println "Verifying outputs..."
        
        if (outdir.exists()) {
            for (dir in outdir.listFiles()) {
                verifyOutput(dir)
            }
        }
        else {
            println "No output artifacts found"
        }
    }
}

new VerifyOutputs().main(args)
