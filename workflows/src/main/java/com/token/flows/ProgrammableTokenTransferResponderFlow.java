package com.token.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(ProgrammableTokenTransferInitiatorFlow.class)
public class ProgrammableTokenTransferResponderFlow extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public ProgrammableTokenTransferResponderFlow(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        return subFlow(new ReceiveFinalityFlow(otherPartySession));
    }
}