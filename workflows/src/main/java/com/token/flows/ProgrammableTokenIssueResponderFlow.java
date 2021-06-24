package com.token.flows;


import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

@InitiatedBy(ProgrammableTokenIssueFlow.class)
public class ProgrammableTokenIssueResponderFlow extends FlowLogic<Void> {
    private final FlowSession otherSide;
    public ProgrammableTokenIssueResponderFlow(FlowSession otherSide) {
        this.otherSide = otherSide;
    }
    @Override
    @Suspendable
    public Void call() throws FlowException {
        SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(otherSide) {
            @Suspendable
            @Override
            protected void checkTransaction(SignedTransaction stx) throws FlowException {
                // Implement responder flow transaction checks here
            }
        });
        subFlow(new ReceiveFinalityFlow(otherSide, signedTransaction.getId()));
        return null;
    }
}
