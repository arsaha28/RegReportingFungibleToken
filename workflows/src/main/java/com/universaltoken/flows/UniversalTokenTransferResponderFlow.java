package com.universaltoken.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(UniversalTokenTransferInitiatorFlow.class)
public class UniversalTokenTransferResponderFlow extends FlowLogic<SignedTransaction> {
    private FlowSession otherPartySession;

    public UniversalTokenTransferResponderFlow(FlowSession otherPartySession) {
        this.otherPartySession = otherPartySession;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        return subFlow(new ReceiveFinalityFlow(otherPartySession));
    }
}