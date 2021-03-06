package com.credits.scapi.v3;

import com.credits.scapi.internal.v0.SmartContractApi;
import pojo.ExternalSmartContract;
import pojo.SmartContractContextData;
import service.executor.ContractExecutorService;
import service.node.NodeApiExecInteractionService;

import java.math.BigDecimal;
import java.util.Map;

import static pojo.SmartContractConstants.getSessionSmartContractConstants;

public abstract class SmartContract extends SmartContractApi {

    private static final long serialVersionUID = -7107388825339899265L;
    private static NodeApiExecInteractionService nodeApiService;
    private static ContractExecutorService contractExecutorService;
    private transient Map<String, ExternalSmartContract> usedContracts;

    protected final transient long accessId;
    protected final transient String initiator;
    protected final String contractAddress;

    public SmartContract() {
        final var contractConstants = getSessionSmartContractConstants(Thread.currentThread().getId());
        initiator = contractConstants.initiator;
        accessId = contractConstants.accessId;
        contractAddress = contractConstants.contractAddress;
        usedContracts = contractConstants.usedContracts;
    }

    final protected void sendTransaction(String from, String to, double amount, byte... userData) {
        nodeApiService.sendTransaction(accessId, from, to, amount, userData);
    }

    final protected Object invokeExternalContract(String contractAddress, String method, Object... params) {
        return contractExecutorService.executeExternalSmartContact(
                new SmartContractContextData(
                        accessId, this.contractAddress, usedContracts, getClass().getClassLoader()),
                contractAddress,
                method,
                params);
    }

    final protected byte[] getSeed() {
        return nodeApiService.getSeed(accessId);
    }

    final protected BigDecimal getBalance(String addressBase58) {
        return nodeApiService.getBalance(addressBase58);
    }

    final protected long getBlockchainTimeMills(){
        return nodeApiService.getBlockchainTimeMills(accessId);
    }
}
