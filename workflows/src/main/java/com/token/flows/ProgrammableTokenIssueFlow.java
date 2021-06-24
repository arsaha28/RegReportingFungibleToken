package com.token.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.token.contracts.ProgrammableTokenContract;
import com.token.states.ProgrammableToken;
import net.corda.core.contracts.CommandData;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.Arrays;

@StartableByRPC
@InitiatingFlow
public class ProgrammableTokenIssueFlow extends FlowLogic<String> {

    private final Party owner;
    private final Party notified;
    private final int amount;

    public ProgrammableTokenIssueFlow(Party owner, Party notified, int amount) {
        this.owner = owner;
        this.notified = notified;
        this.amount = amount;
    }

    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        Party issuer = getOurIdentity();

        final ProgrammableToken tokenState = new ProgrammableToken(issuer,owner,notified,amount);


        TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
        CommandData commandData = new ProgrammableTokenContract.Commands.Issue();
        transactionBuilder.addCommand(commandData, issuer.getOwningKey(), owner.getOwningKey(),notified.getOwningKey());
        transactionBuilder.addOutputState(tokenState, ProgrammableTokenContract.ID);
        transactionBuilder.verify(getServiceHub());

        FlowSession sessionOwner = initiateFlow(owner);
        FlowSession sessionNotified = initiateFlow(notified);

        /*List<FlowSession> sessionList = Collections.singletonList(sessionOwner);
        sessionList.add(sessionNotified);
*/
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, Arrays.asList(sessionNotified,sessionOwner)));
        SignedTransaction stx = subFlow(new FinalityFlow(fullySignedTransaction, Arrays.asList(sessionNotified,sessionOwner)));

        return "\nTransaction ID: "+stx.getId();
    }
}
