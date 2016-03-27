/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.process.internal.child;

import org.gradle.messaging.remote.Address;
import org.gradle.process.internal.AbstractWorkerProcessBuilder;
import org.gradle.process.internal.JavaExecHandleBuilder;

import java.net.URL;
import java.util.List;

public interface WorkerFactory {
    /**
     * Configures the Java command that will be used to launch the child process.
     */
    void prepareJavaCommand(Object workerId, String displayName, AbstractWorkerProcessBuilder processBuilder, List<URL> implementationClassPath, Address serverAddress, JavaExecHandleBuilder execSpec);
}
