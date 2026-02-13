package com.restaurant.userservice.core.rpc;

import com.restaurant.userservice.core.service.user.IUserService;
import com.restaurant.commons.proto.*;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@DubboService(
        version = "1.0.0",
        group = "user-service",
        protocol = "tri"  // Triple protocol
)
@Service
@RequiredArgsConstructor
public class UserServiceRpcImpl extends UserServiceGrpc.UserServiceImplBase {

    private final IUserService userService;
    private final Logger _log = LoggerFactory.getLogger(UserServiceRpcImpl.class);

    @Override
    public void existsByEmail(ExistsByEmailRequest request,
                              io.grpc.stub.StreamObserver<ExistsResponse> responseObserver) {
        try {
            _log.info("RPC: existsByEmail called with email={}", request.getEmail());

            boolean exists = userService.existsByEmail(request.getEmail());

            ExistsResponse response = ExistsResponse.newBuilder()
                    .setExists(exists)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            _log.error("RPC: existsByEmail failed", e);
            responseObserver.onError(
                    io.grpc.Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void existsByPhoneNumber(ExistsByPhoneNumberRequest request,
                              io.grpc.stub.StreamObserver<ExistsResponse> responseObserver) {
        try {
            _log.info("RPC: existsByPhoneNumber called with phone number={}", request.getPhoneNumber());

            boolean exists = userService.existsByPhoneNumber(request.getPhoneNumber());

            ExistsResponse response = ExistsResponse.newBuilder()
                    .setExists(exists)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            _log.error("RPC: existsByPhoneNumber failed", e);
            responseObserver.onError(
                    io.grpc.Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }
}