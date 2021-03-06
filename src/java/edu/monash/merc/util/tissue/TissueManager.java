/*
 * Copyright (c) 2010-2011, Monash e-Research Centre
 * (Monash University, Australia)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of the Monash University nor the names of its
 * 	  contributors may be used to endorse or promote products derived from
 * 	  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.monash.merc.util.tissue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Irina
 * Date: 12/02/13
 * Time: 12:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class TissueManager {

    private static final Object LOCK = new Object();

    public static final long PROCESS_ID = 10000;

    private static Map<Long, TissueProcess> processMap = new HashMap<Long, TissueProcess>();

    public static boolean registerProcess(long processId, TissueProcess process) {
        synchronized (LOCK) {
            long pid = process.getProcessId();
            if (processId != pid) {
            }
            if (findImportProcess(processId)) {
                return false;
            } else {
                processMap.put(processId, process);
                return true;
            }
        }
    }

    public static boolean unRegisterProcess(long pId) {
        synchronized (LOCK) {
            processMap.remove(pId);
            return true;
        }
    }

    public static boolean findImportProcess(long pid) {
        TissueProcess process = processMap.get(pid);
        // can't find the process, just return false;
        if (process == null) {
            return false;
        }
        // if process is finished, just return false;
        if (process.isProcessFinished()) {
            return false;
        } else {
            return true;
        }
    }
}
