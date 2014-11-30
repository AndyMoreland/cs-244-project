/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

# Thrift Tutorial
# Mark Slee (mcslee@facebook.com)
#
# This file aims to teach you how to use Thrift, in a .thrift file. Neato. The
# first thing to notice is that .thrift files support standard shell comments.
# This lets you make your thrift file executable and include your Thrift build
# step on the top line. And you can place comments like this anywhere you like.
#
# Before running this file, you will need to have installed the thrift compiler
# into /usr/local/bin.

/**
 * The first thing to know about are types. The available types in Thrift are:
 *
 *  bool        Boolean, one byte
 *  byte        Signed byte
 *  i16         Signed 16-bit integer
 *  i32         Signed 32-bit integer
 *  i64         Signed 64-bit integer
 *  double      64-bit floating point value
 *  string      String
 *  binary      Blob (byte array)
 *  map<t1,t2>  Map from one type to another
 *  list<t1>    Ordered list of one type
 *  set<t1>     Set of unique elements of one type
 *
 * Did you also notice that Thrift supports C style comments?
 */

/**
 * Thrift files can namespace, package, or prefix their output in various
 * target languages.
 */
namespace java TwoPhaseCommit

/**
 * You can define enums, which are just 32 bit integers. Values are optional
 * and start at 1 if not supplied, C style again.
 */

enum Vote {
    COMMIT = 1,
    ABBORT = 2
}

enum ChineseCheckersOperation {
    MOVE_PIECE = 1,
    KICK_PLAYER = 2,
    ADD_PLAYER = 3;
}

exception InvalidOperation {
    1: i32 errorType,
    2: string errorMessage;
}

struct Operation {
    1: i32 operationId,
    2: i32 operationType, // an ENUM with service-defined semantics
    3: map<string, binary> arguments; // the string corresponds to the argument name and the binary to the argument value
}

struct Transaction {
    1: i32 transactionId,
    2: Operation operation;
}

/**
 * Ahh, now onto the cool part, defining a service. Services just need a name
 * and can optionally inherit from another service using the extends keyword.
 */
service CommitServer {
   i32 requestMessage(1:i32 index, 2:i32 value),
   Vote requestOutcome(1:i32 index)
}

service CommitClient {
   Vote prepare(1:i32 index, 2:i32 value),
   void acceptOutcome(1:i32 index, 2:Vote outcome)
}
