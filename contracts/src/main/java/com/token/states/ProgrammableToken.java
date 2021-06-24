package com.token.states;

import com.token.contracts.ProgrammableTokenContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@BelongsToContract(ProgrammableTokenContract.class)
public class ProgrammableToken implements ContractState {

    private final Party owner;
    private final int amount;
    private final Party issuer;
    private final Party notified;

    public ProgrammableToken(Party issuer, Party owner, Party notified, int amount) {
        this.issuer = issuer;
        this.owner = owner;
        this.amount = amount;
        this.notified = notified;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(issuer, owner,notified);
    }
    public Party getIssuer() {
        return issuer;
    }
    public Party getOwner() {
        return owner;
    }
    public Party getNotified() {
        return notified;
    }
    public int getAmount() {
        return amount;
    }
}
