run vaultQuery contractStateType: com.token.states.ProgrammableToken

//London branch issue 1000 Digital Token to Singapore branch
start ProgrammableTokenIssueFlow owner: MyCryptoBankSingapore, amount: 10000 ,notified: MyCryptoREGSYS

//Singapore branch issue 600 Digital Token to HongKong branch and retains remaining 400
start ProgrammableTokenTransferInitiatorFlow issuer: MyCryptoBankLondon, amount: 600, receiver: MyCryptoBankHongKong,notified: MyCryptoREGSYS

