package com.restaurant.businessservice.core.rpc;

import com.restaurant.commons.core.rpc.storage.CreateFileOnCloudRequest;
import com.restaurant.commons.core.rpc.storage.CreateFileOnCloudResponse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class BusinessServiceRpcClientImpl implements IBusinessServiceRpcClient {
    private static final Logger _log = LoggerFactory.getLogger(BusinessServiceRpcClientImpl.class);

    //@DubboReference

    @Override
    public String createFileOnCloud(MultipartFile file) {
        //
        return "https://localhost:433/uploads/imagesssssss.xxx";
    }

    @Override
    public List<String> createFilesOnCloud(List<MultipartFile> files) {
        return null;
    }
}
