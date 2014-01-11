/*
 * Copyright 2013 - 2014 The Original Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticsoftware.elasticactors;

/**
 * @author Joost van de Wijgerd
 */
public class TestActorState implements ActorState<String,TestActorState> {
    private boolean callSucceeded = false;
    private transient ActorSystem actorSystem = null;
    private ActorRef sender;
    private TestMessage message;

    public TestMessage getMessage() {
        return message;
    }

    public void setMessage(TestMessage message) {
        this.message = message;
    }

    public ActorRef getSender() {
        return sender;
    }

    public void setSender(ActorRef sender) {
        this.sender = sender;
    }

    public ActorSystem getActorSystem() {
        return actorSystem;
    }

    public void setActorSystem(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }



    public boolean isCallSucceeded() {
        return callSucceeded;
    }

    public void setCallSucceeded(boolean callSucceeded) {
        this.callSucceeded = callSucceeded;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public TestActorState getBody() {
        return this;
    }
}