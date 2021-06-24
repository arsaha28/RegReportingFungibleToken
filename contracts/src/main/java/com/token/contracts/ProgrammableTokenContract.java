package com.token.contracts;

import com.token.states.ProgrammableToken;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class ProgrammableTokenContract implements Contract {
    public static String ID = "com.token.contracts.ProgrammableTokenContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        if(tx.getCommands().size() !=1)
            throw new IllegalArgumentException("One Command Expected");
        if(tx.getCommand(0).getValue() instanceof Commands.Issue)
            verifyIssue(tx);
        else if(tx.getCommand(0).getValue() instanceof Commands.Transfer)
            verifyTransfer(tx);
        else
            throw new IllegalArgumentException("Unsupported Command");
    }

    private void verifyIssue(LedgerTransaction tx){
        if(tx.getInputs().size() != 0)
            throw new IllegalArgumentException("Zero Inputs Expected");
        if(tx.getOutputs().size() != 1)
            throw new IllegalArgumentException("One Output Expected");
        if(!(tx.getOutput(0) instanceof ProgrammableToken))
            throw new IllegalArgumentException("Output of type TokenState Expected");
        ProgrammableToken tokenState = (ProgrammableToken)tx.getOutput(0);
        if(tokenState.getAmount() < 1)
            throw new IllegalArgumentException("Positive amount expected");
        if(!(tx.getCommand(0).getSigners()
                .contains(tokenState.getIssuer().getOwningKey())))
            throw new IllegalArgumentException("Issuer must sign");
    }

    private void verifyTransfer(LedgerTransaction tx){
        if(tx.getInputs().size() < 1)
            throw new IllegalArgumentException("More than 0 inputs expected");
        if(!(tx.getOutputs().size() == 1 || tx.getOutputs().size() == 2))
            throw new IllegalArgumentException("Output count must either be one or two");
        AtomicInteger inputSum = new AtomicInteger();
        tx.getInputs().forEach(contractStateStateAndRef -> {
            ProgrammableToken inputState = (ProgrammableToken)contractStateStateAndRef.getState().getData();
            inputSum.set(inputSum.get() + inputState.getAmount());
        });

        AtomicInteger outputSum = new AtomicInteger();
        tx.getOutputs().forEach(contractStateTransactionState -> {
            outputSum.set(outputSum.get() + ((ProgrammableToken)contractStateTransactionState.getData()).getAmount());
        });

        if(inputSum.get() != outputSum.get())
            throw new IllegalArgumentException("Incorrect Spending");
        tx.getCommand(0).getSigners().stream().forEach(key->{
                    System.out.println("Found key :"+key.toString());
                }
        );
        if(!(tx.getCommand(0).getSigners().contains(((ProgrammableToken)tx.getInput(0)).getOwner().getOwningKey())))
            throw new IllegalArgumentException("Owner must Sign");
    }

    public interface Commands extends CommandData {
        class Issue implements Commands { }
        class Transfer implements Commands { }

    }
}
