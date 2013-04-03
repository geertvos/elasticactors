/*
 * Copyright 2013 Joost van de Wijgerd
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

package org.elasterix.elasticactors.examples.pi.actors;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.elasterix.elasticactors.ActorRef;
import org.elasterix.elasticactors.ActorState;
import org.elasterix.elasticactors.ActorStateFactory;
import org.elasterix.elasticactors.UntypedActor;
import org.elasterix.elasticactors.examples.pi.messages.Calculate;
import org.elasterix.elasticactors.examples.pi.messages.PiApproximation;
import org.elasterix.elasticactors.examples.pi.messages.Result;
import org.elasterix.elasticactors.examples.pi.messages.Work;

import java.util.*;

/**
 * @author Joost van de Wijgerd
 */
public final class  Master extends UntypedActor implements ActorStateFactory {
    private static final Logger logger = Logger.getLogger(Master.class);

    @Override
    public ActorState create() {
        throw new IllegalStateException("Please initialize Master with state");
    }

    public static final class State {
        private final ActorRef listener;
        private final int nrOfWorkers;
        private final int nrOfMessages;
        private final int nrOfElements;
        private List<ActorRef> workers;
        private final Map<String,Calculation> calculations;

        private static final class Calculation {
            private final long start;
            private double pi;
            private int nrOfResults;
            private int roundRobinCounter;

            private Calculation() {
                this(System.currentTimeMillis());
            }

            @JsonCreator
            private Calculation(@JsonProperty("start") long start) {
                this.start = start;
            }

            @JsonProperty("start")
            public long getStart() {
                return start;
            }

            public double getPi() {
                return pi;
            }

            public void setPi(double pi) {
                this.pi = pi;
            }

            public int getNrOfResults() {
                return nrOfResults;
            }

            public void setNrOfResults(int nrOfResults) {
                this.nrOfResults = nrOfResults;
            }

            public int getRoundRobinCounter() {
                return roundRobinCounter;
            }

            public void setRoundRobinCounter(int roundRobinCounter) {
                this.roundRobinCounter = roundRobinCounter;
            }
        }

        public State(ActorRef listener, int nrOfWorkers, int nrOfMessages, int nrOfElements) {
            this(listener, nrOfWorkers, nrOfMessages, nrOfElements, new HashMap<String,Calculation>());
        }

        @JsonCreator
        public State(@JsonProperty("listener") ActorRef listener,
                     @JsonProperty("nrOfWorkers") int nrOfWorkers,
                     @JsonProperty("nrOfMessages") int nrOfMessages,
                     @JsonProperty("nrOfElements") int nrOfElements,
                     @JsonProperty("calculations") Map<String,Calculation> calculations) {
            this.listener = listener;
            this.nrOfWorkers = nrOfWorkers;
            this.nrOfMessages = nrOfMessages;
            this.nrOfElements = nrOfElements;
            this.calculations = calculations;
        }

        @JsonProperty("listener")
        public ActorRef getListener() {
            return listener;
        }

        @JsonProperty("workers")
        public List<ActorRef> getWorkers() {
            return workers;
        }

        public void setWorkers(List<ActorRef> workers) {
            this.workers = workers;
        }

        @JsonProperty("nrOfWorkers")
        public int getNrOfWorkers() {
            return nrOfWorkers;
        }

        @JsonProperty("nrOfMessages")
        public int getNrOfMessages() {
            return nrOfMessages;
        }

        @JsonProperty("nrOfElements")
        public int getNrOfElements() {
            return nrOfElements;
        }

        @JsonProperty("calculations")
        public Map<String,Calculation> getCalculations() {
            return calculations;
        }
    }

    @Override
    public void postCreate(ActorRef creator) throws Exception {
        State state = getState(this).getAsObject(State.class);
        List<ActorRef> workers = new ArrayList<ActorRef>(state.nrOfWorkers);
        for (int i = 1; i <= state.nrOfWorkers; i++) {
            workers.add(getSystem().actorOf("worker-"+i,Worker.class));
        }
        state.setWorkers(workers);
    }

    @Override
    public void postActivate(String previousVersion) throws Exception {
        State state = getState(this).getAsObject(State.class);
    }

    public void onReceive(Object message, ActorRef sender) {
        State state = getState(this).getAsObject(State.class);
        if (message instanceof Calculate) {
            Calculate calculateRequest = (Calculate) message;
            State.Calculation calculation = new State.Calculation();
            for (int start = 0; start < state.getNrOfMessages(); start++) {
                state.workers.get(calculation.roundRobinCounter++ % state.nrOfWorkers).tell(new Work(start, state.getNrOfElements(), calculateRequest.getId()), getSelf());
            }
            state.getCalculations().put(calculateRequest.getId(), calculation);
        } else if (message instanceof Result) {
            Result result = (Result) message;
            State.Calculation calculation = state.getCalculations().get(result.getCalculationId());
            calculation.pi += result.getValue();
            calculation.nrOfResults += 1;

            if (calculation.nrOfResults == state.nrOfMessages) {
                // Send the result to the listener
                long duration = System.currentTimeMillis() - calculation.start;
                state.listener.tell(new PiApproximation(calculation.pi, duration), getSelf());
                // remove calculation
                state.getCalculations().remove(result.getCalculationId());
                // Stops this actor and all its supervised children
                // @todo: figure out how to do this
                // getContext().stop(getSelf());
            }
        } else {
            unhandled(message);
        }
    }
}