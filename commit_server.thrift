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
namespace java PBFT

/**
 * You can define enums, which are just 32 bit integers. Values are optional
 * and start at 1 if not supplied, C style again.
 */

typedef binary Signature

enum Vote {
    COMMIT = 1,
    ABBORT = 2
}

enum TChineseCheckersOperation {
    NO_OP = 0,
    MOVE_PIECE = 1,
    KICK_PLAYER = 2,
    ADD_PLAYER = 3;
}

exception InvalidOperation {
    1: i32 errorType,
    2: string errorMessage,
    3: Signature signature;
}

struct TOperation {
    1: i32 operationType, // an ENUM with service-defined semantics
    2: string arguments, // JSON formatted or something
    3: i32 replicaID
}

struct Viewstamp {
    1:i32 sequenceNumber,
    2:i32 viewId;
}

struct TTransaction {
    1:Viewstamp viewstamp,
    2:TOperation operation,
    3:i32 replicaId;
}

struct ClientMessage {
    1:TOperation operation,
    2:i32 replicaId,
    3:Signature messageSignature;
}

struct PrePrepareMessage {
    1:Viewstamp viewstamp,
    2:Signature transactionDigest,
    3:i32 replicaId;
    4:Signature messageSignature;
}

struct PrepareMessage {
    1:Viewstamp viewstamp,
    2:Signature transactionDigest,
    3:i32 replicaId,
    4:Signature messageSignature;
}

struct CommitMessage {
    1:Viewstamp viewstamp,
    2:Signature transactionDigest,
    3:i32 replicaId,
    4:Signature messageSignature;
}

struct CheckpointMessage {
    1:i32 sequenceNumber,
    2:Signature checkpointDigest,
    3:i32 replicaId,
    4:Signature messageSignature;
}

struct ViewChangeMessage { // need 2f+1 distinct ones, distinct = distinct newViewID, replicaID
    1:i32 newViewID,
    2:i32 sequenceNumber,
    3:set<CheckpointMessage> checkpointProof,
    4:list<PrePrepareMessage> preparedGreaterThanSequenceNumber,
    5:list<set<PrepareMessage>> prepareMessages, // this is just proof for 4
    6:i32 replicaID, // sender of message
    7:Signature messageSignature;
}

struct NewViewMessage {
    1:i32 newViewID,
    2:set<ViewChangeMessage> viewChangeMessages,
    3:list<PrePrepareMessage> prePrepareMessages,
    5:i32 replicaID,
    6:Signature messageSignature;
}

struct AskForTransaction {
    1:Viewstamp viewstamp,
    2:i32 replicaID;
}

/**
 * Ahh, now onto the cool part, defining a service. Services just need a name
 * and can optionally inherit from another service using the extends keyword.
 */

service PBFTCohort {
    void clientMessage(1:ClientMessage message),
    void prePrepare(1:PrePrepareMessage message, 2:ClientMessage clientMessage, 3:TTransaction transaction),
    void prepare(1:PrepareMessage message),
    void commit(1:CommitMessage message),
    void checkpoint(1:CheckpointMessage message),
    void initiateViewChange(),
    void startViewChange(1:ViewChangeMessage message),
    void approveViewChange(1:NewViewMessage message),
    TTransaction getTransaction(1:AskForTransaction message),
    void ping();
}
