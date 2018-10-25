package com.credits.service.contract;

import com.credits.ApplicationProperties;
import com.credits.classload.ByteArrayContractClassLoader;
import com.credits.common.utils.Base58;
import com.credits.exception.ContractExecutorException;
import com.credits.leveldb.client.ApiClient;
import com.credits.leveldb.client.exception.LevelDbClientException;
import com.credits.leveldb.client.util.LevelDbClientConverter;
import com.credits.secure.Sandbox;
import com.credits.service.db.leveldb.LevelDbInteractionService;
import com.credits.thrift.DeployReturnValue;
import com.credits.thrift.MethodDescription;
import com.credits.thrift.ReturnValue;
import com.credits.thrift.generated.Variant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.lang.reflect.Type;
import java.net.NetPermission;
import java.net.SocketPermission;
import java.security.Permissions;
import java.security.SecurityPermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.stream.Collectors;

import static com.credits.ioc.Injector.INJECTOR;
import static com.credits.serialise.Serializer.deserialize;
import static com.credits.serialise.Serializer.serialize;
import static com.credits.thrift.utils.ContractUtils.deployAndGetContractVariables;
import static com.credits.thrift.utils.ContractUtils.getContractVariables;
import static com.credits.thrift.utils.ContractUtils.mapObjectToVariant;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

public class ContractExecutorServiceImpl implements ContractExecutorService {

    private final static Logger logger = LoggerFactory.getLogger(ContractExecutorServiceImpl.class);

    @Inject
    public ApplicationProperties properties;

    @Inject
    public LevelDbInteractionService dbInteractionService;

    @Inject
    public ApiClient apiClient;

    public ContractExecutorServiceImpl() {
        INJECTOR.component.inject(this);
        try {
            Class<?> contract = Class.forName("SmartContract");
            Field interactionService = contract.getDeclaredField("service");
            interactionService.setAccessible(true);
            interactionService.set(null, dbInteractionService);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            logger.error("Cannot load smart contract's super class", e);
        }
    }

    @Override
    public ReturnValue execute(byte[] initiatorAddress, byte[] bytecode, byte[] objectState, String methodName,
        Variant[] params) throws ContractExecutorException, LevelDbClientException {

        ByteArrayContractClassLoader classLoader = new ByteArrayContractClassLoader();
        Class<?> contractClass = classLoader.buildClass(bytecode);

        String initiator = Base58.encode(initiatorAddress);

        Object contractInstance;
        if (objectState != null && objectState.length != 0) {
            contractInstance = deserialize(objectState, classLoader);
        } else {
            DeployReturnValue deployReturnValue = deployAndGetContractVariables(contractClass, initiator);
            return new ReturnValue(deployReturnValue.getContractState(), null,
                deployReturnValue.getContractVariables());
        }

        initializeInitiator(initiator, contractClass, contractInstance);

        List<Method> methods = getMethods(contractClass, methodName, params);

        Method targetMethod = null;
        Object[] argValues = null;
        if (methods.isEmpty()) {
            throw new ContractExecutorException("Cannot execute the contract: " + initiator +
                ". Reason: Cannot find a method by name and parameters specified");
        } else {
            for (Method method : methods) {
                try {
                    Class<?>[] types = method.getParameterTypes();
                    if (types.length > 0) {
                        argValues = castValues(types, params);
                    }
                } catch (ClassCastException e) {
                    continue;
                } catch (ContractExecutorException e) {
                    throw new ContractExecutorException(
                        "Cannot execute the contract: " + initiator + "Reason: " + getRootCauseMessage(e), e);
                }
                targetMethod = method;
                break;
            }
        }

        if (targetMethod == null) {
            throw new ContractExecutorException("Cannot execute the contract: " + initiator +
                ". Reason: Cannot cast parameters to the method found by name: " + methodName);
        }

        //Invoking target method
        Object returnObject;
        Class<?> returnType = targetMethod.getReturnType();
        try {
            Sandbox.confine(contractClass, createPermissions());
            returnObject = targetMethod.invoke(contractInstance, argValues);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ContractExecutorException(
                "Cannot execute the contract: " + initiator + ". Reason: " + getRootCauseMessage(e));
        }

        Variant returnValue = null;
        if (returnType != void.class) {
            returnValue = mapObjectToVariant(returnObject);
        }

        Map<String, Variant> contractVariables = getContractVariables(contractInstance);

        return new ReturnValue(serialize(initiator, contractInstance), returnValue, contractVariables);
    }

    @Override
    public List<MethodDescription> getContractsMethods(byte[] bytecode) {
        ByteArrayContractClassLoader classLoader = new ByteArrayContractClassLoader();
        Class<?> contractClass = classLoader.buildClass(bytecode);

        List<MethodDescription> result = new ArrayList<>();
        for (Method method : contractClass.getMethods()) {
            ArrayList<String> argTypes = new ArrayList<>();
            for (Type type : method.getGenericParameterTypes()) {
                argTypes.add(type.getTypeName());
            }
            result.add(new MethodDescription(method.getName(), argTypes, method.getGenericReturnType().getTypeName()));
        }

        return result;
    }

    private List<Method> getMethods(Class<?> contractClass, String methodName, Variant[] params) {
        return Arrays.stream(contractClass.getMethods()).filter(method -> {
            if (params == null || params.length == 0) {
                return method.getName().equals(methodName) && method.getParameterCount() == 0;
            } else {
                return method.getName().equals(methodName) && method.getParameterCount() == params.length;
            }
        }).collect(Collectors.toList());
    }

    private void initializeInitiator(String initiator, Class<?> clazz, Object instance) {
        try {
            Field initiatorField = clazz.getField("initiator");
            initiatorField.setAccessible(true);
            initiatorField.set(instance, initiator);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("Cannot initialize \"initiator\" field. Reason:" + e.getMessage());
        }
    }

    private Permissions createPermissions() {
        Permissions permissions = new Permissions();
        permissions.add(new ReflectPermission("suppressAccessChecks"));
        permissions.add(new SocketPermission(properties.apiHost + ":" + properties.apiPort, "connect,listen,resolve"));
        permissions.add(new NetPermission("getProxySelector"));
        permissions.add(new RuntimePermission("readFileDescriptor"));
        permissions.add(new RuntimePermission("writeFileDescriptor"));
        permissions.add(new RuntimePermission("accessDeclaredMembers"));
        permissions.add(new RuntimePermission("accessClassInPackage.sun.security.ec"));
        permissions.add(new RuntimePermission("accessClassInPackage.sun.security.rsa"));
        permissions.add(new RuntimePermission("accessClassInPackage.sun.security.provider"));
        permissions.add(new RuntimePermission("java.lang.RuntimePermission", "loadLibrary.sunec"));
        permissions.add(new SecurityPermission("getProperty.networkaddress.cache.ttl", "read"));
        permissions.add(new SecurityPermission("getProperty.networkaddress.cache.negative.ttl", "read"));
        permissions.add(new SecurityPermission("getProperty.jdk.jar.disabledAlgorithms"));
        permissions.add(new SecurityPermission("putProviderProperty.SunRsaSign"));
        permissions.add(new SecurityPermission("putProviderProperty.SUN"));
        permissions.add(new PropertyPermission("sun.net.inetaddr.ttl", "read"));
        permissions.add(new PropertyPermission("socksProxyHost", "read"));
        permissions.add(new PropertyPermission("java.net.useSystemProxies", "read"));
        permissions.add(new PropertyPermission("java.home", "read"));
        permissions.add(new PropertyPermission("com.sun.security.preserveOldDCEncoding", "read"));
        permissions.add(new PropertyPermission("sun.security.key.serial.interop", "read"));
        permissions.add(new PropertyPermission("sun.security.rsa.restrictRSAExponent", "read"));
        return permissions;
    }

    private Object[] castValues(Class<?>[] types, Variant[] params)
        throws ContractExecutorException, LevelDbClientException {
        if (params == null || params.length != types.length) {
            throw new ContractExecutorException("Not enough arguments passed");
        }
        Object[] retVal = new Object[types.length];
        int i = 0;
        Variant param;
        for (Class<?> type : types) {
            param = params[i];
            if (type.isArray()) {
                if (types.length > 1) {
                    throw new ContractExecutorException("Having array with other parameter types is not supported");
                }
            }

            retVal[i] = LevelDbClientConverter.parseObjectFromVariant(param);
            logger.info(String.format("param[%s] = %s", i, retVal[i]));
            i++;
        }
        return retVal;
    }
}