package org.elasticsoftware.elasticactors.eventsourcing;

import org.elasticsoftware.elasticactors.Actor;
import org.elasticsoftware.elasticactors.ActorRef;
import org.elasticsoftware.elasticactors.ActorSystem;
import org.elasticsoftware.elasticactors.base.serialization.JacksonSerializationFramework;
import org.elasticsoftware.elasticactors.cqrs.*;
import org.elasticsoftware.elasticactors.eventsourcing.commands.*;
import org.elasticsoftware.elasticactors.eventsourcing.events.AccountCreditedEvent;
import org.elasticsoftware.elasticactors.eventsourcing.events.AccountDebitedEvent;
import org.elasticsoftware.elasticactors.eventsourcing.queries.BalanceQuery;
import org.elasticsoftware.elasticactors.eventsourcing.queries.BalanceQueryResponse;
import org.elasticsoftware.elasticactors.eventsourcing.queries.VirtualBankAccountAdapter;

import java.math.BigDecimal;

@Actor(stateClass = VirtualBankAccountState.class, serializationFramework = JacksonSerializationFramework.class)
public class VirtualBankAccountActor extends EventSourcedActor {
    public VirtualBankAccountActor() {
        super(ReadOnlyVirtualBankAccountState.class);
        // register all handlers here
        registerCommandHandler(TransferCommand.class, (CommandHandler<CommandResponse, TransferCommand, ReadOnlyVirtualBankAccountState>) this::handleTransfer);
        registerCommandHandler(CreditAccountCommand.class, (CommandHandler<CommandResponse, CreditAccountCommand, ReadOnlyVirtualBankAccountState>) this::handleCreditAccount);
        registerEventHandler(AccountDebitedEvent.class, (SourcedEventHandler<AccountDebitedEvent, VirtualBankAccountState>) this::debitBalance);
        registerEventHandler(AccountCreditedEvent.class, (SourcedEventHandler<AccountCreditedEvent, VirtualBankAccountState>) this::creditBalance);
        registerQueryHandler(BalanceQuery.class, (QueryHandler<BalanceQueryResponse, BalanceQuery, ReadOnlyVirtualBankAccountState>) this::balanceQuery);
    }

    @Override
    protected CommandResponse doHandleCommand(Command command) {
        return super.doHandleCommand(command);
    }

    @Override
    protected void commitCommandContext(CommandContext context) {
        //
    }

    public CommandResponse handleTransfer(TransferCommand command, ReadOnlyVirtualBankAccountState state, ActorSystem actorSystem) {
        if(state.getBalance().subtract(command.getAmount()).compareTo(BigDecimal.ZERO) >= 0) {
            apply(new AccountDebitedEvent(command.getAmount()));
            actorSystem.actorFor("accounts/"+command.getToAccount())
                    .ask(new CreditAccountCommand(command.getFromAccount(), command.getToAccount(), command.getAmount()),
                            TransferCompletedResponse.class);
            return new TransferCompletedResponse();
        } else {
            return new TransferDeclinedResponse();
        }
    }

    public CommandResponse handleCreditAccount(CreditAccountCommand command, ReadOnlyVirtualBankAccountState state, ActorSystem actorSystem) {
        apply(new AccountCreditedEvent(command.getAmount()));
        return new TransferCompletedResponse();
    }


    public void debitBalance(AccountDebitedEvent debitEvent, VirtualBankAccountState state) {
        // do the actual debit on the state (no need to check, this has been done in the handleTransfer
        state.debitBalance(debitEvent.getAmount());
    }

    public void creditBalance(AccountCreditedEvent creditedEvent, VirtualBankAccountState state) {
        state.creditBalance(creditedEvent.getAmount());
    }

    public BalanceQueryResponse balanceQuery(BalanceQuery query, ReadOnlyVirtualBankAccountState state, ActorSystem actorSystem) {
        return new BalanceQueryResponse(new VirtualBankAccountAdapter(state));
    }
}