package com.project.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);

    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(
            // localhost:9001/BillingService/CreatePatientAccount
            // aws.grpc:23416/BillingService/CreatePatientAccount
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int serverPort) {

        log.info("Connecting to Billing Service GRPC service at {}:{}",
                serverAddress, serverPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress,
                serverPort).usePlaintext().build();

        blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public void createBillingAccount(
            String patientId, String patientName, String email
    ) {
        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setName(patientName)
                .setEmail(email)
                .build();

        BillingResponse response = blockingStub.createBillingAccount(request);

        log.info("Received response from billing service via GRPC: {}", response);

    }
}
