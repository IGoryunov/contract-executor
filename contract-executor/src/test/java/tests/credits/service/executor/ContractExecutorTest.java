package tests.credits.service.executor;


import com.credits.client.executor.thrift.generated.apiexec.GetSeedResult;
import com.credits.client.node.thrift.generated.WalletBalanceGetResult;
import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.GeneralConverter;
import com.credits.general.util.compiler.CompilationException;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import pojo.EmitTransactionData;
import pojo.ReturnValue;
import tests.credits.UseContract;
import tests.credits.service.ContractExecutorTestContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static com.credits.general.pojo.ApiResponseCode.FAILURE;
import static com.credits.general.pojo.ApiResponseCode.SUCCESS;
import static com.credits.general.thrift.generated.Variant._Fields.V_INT;
import static com.credits.general.thrift.generated.Variant._Fields.V_VOID;
import static com.credits.general.util.variant.VariantConverter.VOID_TYPE_VALUE;
import static com.credits.general.util.variant.VariantConverter.toObject;
import static com.credits.utils.ContractExecutorServiceUtils.SUCCESS_API_RESPONSE;
import static java.nio.ByteBuffer.wrap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static tests.credits.TestContract.*;

public class ContractExecutorTest extends ContractExecutorTestContext {

    @BeforeEach
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("void return value must be return V_VOID variant type")
    void returnVoidType() {
        ReturnValue returnValue = executeSmartContract(smartContract, deployContractState, "initialize");
        assertThat(returnValue.executeResults.get(0).result, is(new Variant(V_VOID, VOID_TYPE_VALUE)));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("getter method cannot change contract state")
    void getterMethodCanNotChangeContractState() {
        ReturnValue rv = executeSmartContract(smartContract, deployContractState, "getTotal");
        assertThat(deployContractState, equalTo(rv.newContractState));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("setter method should be change executor state")
    void saveStateSmartContract() {
        var newContractState = executeSmartContract(smartContract, deployContractState, "addTokens", 10).newContractState;
        assertThat(deployContractState, not(equalTo(newContractState)));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("different contract state must be return different field value")
    public void differentContractStateReturnDifferentResult() {
        var total = getFirstReturnValue(executeSmartContract(smartContract, deployContractState, "getTotal")).getV_int();
        assertThat(total, is(0));

        final var contractState = executeSmartContract(smartContract, deployContractState, "addTokens", 10).newContractState;

        total = getFirstReturnValue(executeSmartContract(smartContract, contractState, "getTotal")).getV_int();
        assertThat(total, is(10));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("initiator must be initialized")
    void initiatorInit() {
        String initiator = getFirstReturnValue(executeSmartContract(smartContract, deployContractState, "getInitiatorAddress")).getV_string();
        assertThat(initiator, is(initiatorAddressBase58));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("sendTransaction into smartContract must be call NodeApiExecService")
    void sendTransactionIntoContract() {
        executeSmartContract(smartContract, deployContractState, "createTransactionIntoContract", "10");
        verify(spyNodeApiExecService)
                .sendTransaction(accessId, initiatorAddressBase58, smartContract.getContractAddressBase58(), 10, 1.0, new byte[0]);
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("getContractVariables must be return public variables of contract")
    void getContractVariablesTest() {
        Map<String, Variant> contractVariables = ceService.getContractVariables(smartContract.getByteCodeObjectDataList(), deployContractState);
        assertThat(contractVariables, IsMapContaining.hasEntry("total", new Variant(V_INT, 0)));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("returned value should be BigDecimal type")
    void getBalanceReturnBigDecimal() {
        final var expectedBigDecimalValue = new BigDecimal("19.5").setScale(18, RoundingMode.DOWN);

        when(nodeThriftApiExec.getBalance(any())).thenReturn(new WalletBalanceGetResult(SUCCESS_API_RESPONSE,
                                                                                        GeneralConverter.bigDecimalToAmount(expectedBigDecimalValue)));

        final var balance = toObject(getFirstReturnValue(executeSmartContract(smartContract, deployContractState, "getBalanceTest")));
        assertThat(balance, is(expectedBigDecimalValue));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("parallel multiple call changes the contract state only once")
    void multipleMethodCall() {
        final var newContractState = executeSmartContractMultiple(smartContract,
                                                                  deployContractState,
                                                                  "addTokens",
                                                                  new Object[][]{{10}, {10}, {10}, {10}}).newContractState;
        assertThat(newContractState, not(equalTo(deployContractState)));

        final var total = ceService.getContractVariables(smartContract.getByteCodeObjectDataList(), newContractState).get("total").getV_int();
        assertThat(total, is(10));
    }


    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("compile source code must return byteCodeDataObjects")
    void compileClassCall() throws CompilationException {
        final var byteCodeObjectData = ceService.compileContractClass(smartContract.getSourceCode());

        assertThat(byteCodeObjectData, is(smartContract.getByteCodeObjectDataList()));
    }


    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("compileContractClass must be return byteCodes list of root and internal classes")
    void compileContractTest() {
        final var result = ceService.compileContractClass(smartContract.getSourceCode());

        assertThat(result.size(), greaterThan(0));
        assertThat(result.get(0).getName(), is("SmartContractV0TestImpl$Geo"));
        assertThat(result.get(1).getName(), is("SmartContractV0TestImpl"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("compileContractClass must be throw compilation exception with explanations")
    void compileContractTest1() {
        assertThrows(CompilationException.class, () -> ceService.compileContractClass("class MyContract {\n MyContract()\n}"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("getSeed must be call NodeApiExecService")
    void getSeedCallIntoSmartContract() {
        final var expectedSeed = new byte[]{0xB, 0xA, 0xB, 0xE};

        when(nodeThriftApiExec.getSeed(anyLong())).thenReturn(new GetSeedResult(SUCCESS_API_RESPONSE, wrap(expectedSeed)));
        final var seed = getFirstReturnValue(executeSmartContract(smartContract, deployContractState, "testGetSeed")).getV_byte_array();

        assertThat(seed, is(expectedSeed));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("execution of smart-executor must be stop when execution time expired")
    void executionTimeTest() {
        final var executionStatus = executeSmartContract(smartContract, deployContractState, 10, "infiniteLoop").executeResults.get(0).status;

        assertThat(executionStatus.code, is(FAILURE.code));
        assertThat(executionStatus.message, containsString("TimeoutException"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("correct interrupt smart executor if time expired")
    void correctInterruptContractIfTimeExpired() {
        final var executionResult = executeSmartContract(smartContract, deployContractState, 10, "interruptedInfiniteLoop").executeResults.get(0);

        assertThat(executionResult.status, is(SUCCESS_API_RESPONSE));
        assertThat(executionResult.result.getV_string(), is("infinite loop interrupted correctly"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("wait a bit delay for correct complete smart executor method")
    void waitCorrectCompleteOfSmartContract() {
        final var executionResult =
                executeSmartContract(smartContract, deployContractState, 10, "interruptInfiniteLoopWithDelay").executeResults.get(0);

        assertThat(executionResult.status, is(SUCCESS_API_RESPONSE));
        assertThat(executionResult.result.getV_string(), is("infinite loop interrupted correctly"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("executeByteCode must be return spent cpu time by execution method thread")
    void executeByteCodeMeasureCpuTimeByThread0() {
        var spentCpuTime = executeSmartContract(smartContract, deployContractState, 11, "nothingWorkOnlySleep").executeResults.get(0).spentCpuTime;
        assertThat(spentCpuTime, lessThan(1000_000L));

        spentCpuTime = executeSmartContract(smartContract, deployContractState, 11, "bitWorkingThenSleep").executeResults.get(0).spentCpuTime;
        assertThat(spentCpuTime, greaterThan(10_000_000L));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("exception into executeByteCode must be return fail status with exception message")
    void exceptionDuringExecution() {
        final var result = executeSmartContract(smartContract, deployContractState, 1, "thisMethodThrowsExcetion").executeResults.get(0);

        assertThat(result.status.code, is(FAILURE.code));
        assertThat(result.status.message, containsString("oops some problem"));
    }

    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("exception into constructor must be return fail status with exception method")
    void constructorWithException() throws IOException {
        final var smartContract = smartContractsRepository.get(TroubleConstructor);

        final var result = deploySmartContract(smartContract).executeResults.get(0);

        assertThat(result.status.code, is(FAILURE.code));
        assertThat(result.status.message, containsString("some problem found here"));
    }

    @Test
    @DisplayName("v2.SmartContract must be compiled and executable")
    @UseContract(SmartContractV2TestImpl)
    void executePayableSmartContractV2() throws IOException {
        final var result = executeSmartContract(smartContract, deployContractState, "payable", BigDecimal.ONE, new byte[0]).executeResults.get(0);

        assertThat(result.status.code, is(SUCCESS.code));
        assertThat(result.result.getV_string(), is("payable call successfully"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("buildContractClass must be return list of classes")
    void buildContractClass() {
        final var result = ceService.buildContractClass(smartContract.getByteCodeObjectDataList());

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getName(), is("SmartContractV0TestImpl$Geo"));
        assertThat(result.get(1).getName(), is("SmartContractV0TestImpl"));
    }

    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("emitted transactions list must be returned into each execution result")
    void returnEmittedTransactions() {
        final var result = executeSmartContract(smartContract, deployContractState, "createTwoTransactions");
        final var emittedTransactions = result.executeResults.get(0).emittedTransactions;

        final var firstTransaction = new EmitTransactionData(initiatorAddressBase58, smartContract.getContractAddressBase58(), 10);
        final var secondTransaction = new EmitTransactionData(initiatorAddressBase58, smartContract.getContractAddressBase58(), 0.01,
                                                              "hello".getBytes());
        assertThat(emittedTransactions.size(), is(2));
        assertThat(emittedTransactions.get(0), is(firstTransaction));
        assertThat(emittedTransactions.get(1), is(secondTransaction));
    }

    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("takeAwayEmittedTransactions must be call even exception occurred")
    void takeAwayTransactionsMustBeCalledAlways() {
        executeSmartContract(smartContract, deployContractState, "createTwoTransactionThenThrowException");

        verify(spyNodeApiExecService, times(2)).takeAwayEmittedTransactions(anyLong());
    }

    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("emitted transaction from external contracts must returned into execution result")
    void returnEmittedTransactionsFromExternalContracts() {
        final var contractAddress = smartContract.getContractAddressBase58();

        final var result = executeSmartContract(smartContract,
                                                deployContractState,
                                                "externalCall",
                                                contractAddress,
                                                "createTwoTransactions");

        final var emittedTransactions = result.executeResults.get(0).emittedTransactions;

        final var firstTransaction = new EmitTransactionData(initiatorAddressBase58, smartContract.getContractAddressBase58(), 10);
        final var secondTransaction = new EmitTransactionData(initiatorAddressBase58, smartContract.getContractAddressBase58(), 0.01,
                                                              "hello".getBytes());
        assertThat(emittedTransactions.size(), is(2));
        assertThat(emittedTransactions.get(0), is(firstTransaction));
        assertThat(emittedTransactions.get(1), is(secondTransaction));
    }

    @Test
    @DisplayName("token name can't be called Credits, CS, etc")
    void cantBeUsedReservedTokenName() {
        // TODO: 2019-07-05 add implementation
    }
}
