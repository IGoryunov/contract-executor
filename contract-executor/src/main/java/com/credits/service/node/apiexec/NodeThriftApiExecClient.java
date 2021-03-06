package com.credits.service.node.apiexec;

import com.credits.client.executor.thrift.generated.apiexec.*;
import com.credits.client.node.thrift.generated.Transaction;
import com.credits.client.node.thrift.generated.WalletBalanceGetResult;
import com.credits.client.node.thrift.generated.WalletIdGetResult;
import com.credits.exception.ApiClientException;
import com.credits.general.thrift.ThriftClientPool;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class NodeThriftApiExecClient implements NodeThriftApiExec {
    private final ThriftClientPool<APIEXEC.Client> pool;
    private final static Logger logger = LoggerFactory.getLogger(NodeThriftApiExecClient.class);

    public NodeThriftApiExecClient(String apiServerHost, Integer apiServerPort) {
        pool = new ThriftClientPool<>(APIEXEC.Client::new, apiServerHost, apiServerPort);

        logger.info("Connection to node on address {}:{}", apiServerHost, apiServerPort);
    }

    @Override
    public GetSeedResult getSeed(long accessId) throws ApiClientException {
        APIEXEC.Client client = pool.getResource();
        return callThrift(client, () -> client.GetSeed(accessId));
    }

    @Override
    public SmartContractGetResult getSmartContractBinary(long accessId, byte[] address) throws ApiClientException {
        APIEXEC.Client client = pool.getResource();
        return callThrift(client, () -> client.SmartContractGet(accessId, ByteBuffer.wrap(address)));
    }


    @Override
    public SendTransactionResult sendTransaction(long accessId, Transaction transaction) throws ApiClientException {
        APIEXEC.Client client = pool.getResource();
        return callThrift(client, () -> client.SendTransaction(accessId, transaction));
    }

    @Override
    public WalletIdGetResult getWalletId(long accessId, byte[] address) throws ApiClientException {
        APIEXEC.Client client = pool.getResource();
        return callThrift(client, () -> client.WalletIdGet(accessId, ByteBuffer.wrap(address)));
    }

    @Override
    public WalletBalanceGetResult getBalance(byte[] address) throws ApiClientException {
        APIEXEC.Client client = pool.getResource();
        return callThrift(client, () -> client.WalletBalanceGet(ByteBuffer.wrap(address)));
    }

    @Override
    public GetDateTimeResult getDateTime(long accessId) throws ApiClientException {
        APIEXEC.Client client = pool.getResource();
        return callThrift(client, () -> client.GetDateTime(accessId));
    }

    private <R> R callThrift(APIEXEC.Client client, Function<R> method) throws ApiClientException {
        try {
            R res = method.apply();
            pool.returnResource(client);
            return res;
        } catch (TException e) {
            pool.returnBrokenResource(client);
            throw new ApiClientException(e);
        }
    }

    private interface Function<R> {
        R apply() throws TException;
    }
}